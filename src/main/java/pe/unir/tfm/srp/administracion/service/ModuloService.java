package pe.unir.tfm.srp.administracion.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.unir.tfm.srp.administracion.config.CurrentUserResolver;
import pe.unir.tfm.srp.administracion.dto.conversor.ModuloConversor;
import pe.unir.tfm.srp.administracion.dto.request.ModuloCrearRequest;
import pe.unir.tfm.srp.administracion.dto.request.ModuloEstadoRequest;
import pe.unir.tfm.srp.administracion.dto.request.ModuloActualizarRequest;
import pe.unir.tfm.srp.administracion.dto.request.ModuloQueryParams;
import pe.unir.tfm.srp.administracion.dto.response.ModuloResponse;
import pe.unir.tfm.srp.administracion.dto.response.PageData;
import pe.unir.tfm.srp.administracion.exception.ConflictoNegocioException;
import pe.unir.tfm.srp.administracion.exception.RecursoNoEncontradoException;
import pe.unir.tfm.srp.administracion.model.Modulo;
import pe.unir.tfm.srp.administracion.repository.ModuloMapper;
import pe.unir.tfm.srp.administracion.repository.RolMapper;

@Service
@RequiredArgsConstructor
public class ModuloService {

    private final ModuloMapper moduloMapper;
    private final ModuloConversor moduloConversor;
    private final CurrentUserResolver currentUserResolver;
    private final RolMapper rolMapper;

    public List<ModuloResponse> listAll() {
        return moduloConversor.aModuloResponseList(moduloMapper.listAll());
    }

    public ModuloResponse findById(UUID id) {
        Modulo modulo = moduloMapper.findById(id);
        if (modulo == null) {
            throw new RecursoNoEncontradoException("Modulo " + id + " no encontrado");
        }
        return moduloConversor.aModuloResponse(modulo);
    }

    public List<ModuloResponse> listByRole(UUID rolId) {
        return moduloConversor.aModuloResponseList(moduloMapper.listByRole(rolId));
    }

    public PageData<ModuloResponse> listPage(ModuloQueryParams params) {
        String sortClause = params.resolveSortClause();
        long total = moduloMapper.countPaged(
            params.getSearch(), params.getSeccion(), params.getEstado());
        List<Modulo> items = moduloMapper.listPage(
            params.getSearch(), params.getSeccion(), params.getEstado(),
            params.getPageSize(), params.offset(), sortClause);
        return PageData.of(
            moduloConversor.aModuloResponseList(items),
            total,
            params.getPage(),
            params.getPageSize()
        );
    }

    @Transactional
    public ModuloResponse create(ModuloCrearRequest request) {
        if (moduloMapper.findByCode(request.codigo()) != null) {
            throw new ConflictoNegocioException("Ya existe un modulo con el codigo " + request.codigo());
        }
        String tipo = request.tipo();
        UUID moduloPadreId = resolverPadreDesdeSeccion(request.seccion(), tipo);
        Modulo nuevo = Modulo.builder()
                .id(UUID.randomUUID())
                .codigo(request.codigo())
                .nombre(request.nombre())
                .icono(request.icono())
                .orden(request.orden())
                .seccion(request.seccion())
                .tipo(tipo)
                .sistema(false)
                .moduloPadreId(moduloPadreId)
                .descripcion(request.descripcion())
                .build();
        moduloMapper.insert(nuevo);
        return moduloConversor.aModuloResponse(nuevo);
    }

    @Transactional
    public ModuloResponse update(UUID id, ModuloActualizarRequest request) {
        Modulo existente = moduloMapper.findById(id);
        if (existente == null) {
            throw new RecursoNoEncontradoException("Modulo " + id + " no encontrado");
        }
        String tipo = request.tipo();
        UUID moduloPadreId = resolverPadreDesdeSeccion(request.seccion(), tipo);
        existente.setNombre(request.nombre());
        existente.setIcono(request.icono());
        existente.setOrden(request.orden());
        existente.setSeccion(request.seccion());
        existente.setTipo(tipo);
        existente.setModuloPadreId(moduloPadreId);
        existente.setDescripcion(request.descripcion());
        moduloMapper.update(existente);
        return moduloConversor.aModuloResponse(existente);
    }

    private UUID resolverPadreDesdeSeccion(String seccion, String tipo) {
        if ("SECTION".equals(tipo)) {
            return null;
        }
        Modulo seccionModulo = moduloMapper.findSectionByCode(seccion);
        if (seccionModulo == null) {
            throw new RecursoNoEncontradoException("Seccion " + seccion + " no encontrada");
        }
        return seccionModulo.getId();
    }

    /**
     * Unico metodo para habilitar/deshabilitar. El registro nunca se borra.
     *  - estado=1: rehabilita el modulo (motivo opcional, se limpia si viene).
     *  - estado=0: deshabilita (motivo obligatorio, se conserva fecha_eliminacion).
     */
    @Transactional
    public ModuloResponse changeState(UUID id, ModuloEstadoRequest request) {
        if (request.estado() == null || (request.estado() != 0 && request.estado() != 1)) {
            throw new ConflictoNegocioException(
                    "El estado debe ser 0 (deshabilitado) o 1 (habilitado)");
        }
        if (request.estado() == 0) {
            String motivo = request.motivoEliminacion();
            if (motivo == null || motivo.trim().length() < 5) {
                throw new ConflictoNegocioException(
                        "Para deshabilitar un modulo, el motivo debe tener al menos 5 caracteres");
            }
            if (rolMapper.countUsosComoLanding(id) > 0) {
                throw new ConflictoNegocioException(
                        "No se puede deshabilitar el modulo porque esta configurado como pagina de inicio de uno o mas roles");
            }
        }

        Modulo existente = moduloMapper.findByIdWithState(id);
        if (existente == null) {
            throw new RecursoNoEncontradoException("Modulo " + id + " no encontrado");
        }
        if (Boolean.TRUE.equals(existente.getSistema())) {
            throw new ConflictoNegocioException(
                    "El modulo " + existente.getCodigo() + " es del sistema y no puede cambiarse de estado");
        }

        UUID usuario = currentUserResolver.obtenerUsuarioActualId();
        String motivo = request.estado() == 0
                ? request.motivoEliminacion().trim()
                : null;

        moduloMapper.changeState(id, request.estado(), usuario, motivo);
        Modulo actualizado = moduloMapper.findByIdWithState(id);
        return moduloConversor.aModuloResponse(actualizado);
    }
}