package pe.unir.tfm.srp.administracion.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.unir.tfm.srp.administracion.config.CurrentUserResolver;
import pe.unir.tfm.srp.administracion.dto.conversor.ModuloConversor;
import pe.unir.tfm.srp.administracion.dto.conversor.RolConversor;
import pe.unir.tfm.srp.administracion.dto.request.AsignarModulosRolRequest;
import pe.unir.tfm.srp.administracion.dto.request.EliminacionRequest;
import pe.unir.tfm.srp.administracion.dto.request.RolActualizarRequest;
import pe.unir.tfm.srp.administracion.dto.request.RolCrearRequest;
import pe.unir.tfm.srp.administracion.dto.request.RolQueryParams;
import pe.unir.tfm.srp.administracion.dto.response.ModuloResponse;
import pe.unir.tfm.srp.administracion.dto.response.PageData;
import pe.unir.tfm.srp.administracion.dto.response.RolResponse;
import pe.unir.tfm.srp.administracion.exception.ConflictoNegocioException;
import pe.unir.tfm.srp.administracion.exception.RecursoNoEncontradoException;
import pe.unir.tfm.srp.administracion.model.Modulo;
import pe.unir.tfm.srp.administracion.model.Rol;
import pe.unir.tfm.srp.administracion.model.RolModulo;
import pe.unir.tfm.srp.administracion.repository.ModuloMapper;
import pe.unir.tfm.srp.administracion.repository.RolMapper;
import pe.unir.tfm.srp.administracion.repository.RolModuloMapper;

@Service
@RequiredArgsConstructor
public class RolService {

    private final RolMapper rolMapper;
    private final ModuloMapper moduloMapper;
    private final RolModuloMapper rolModuloMapper;
    private final RolConversor rolConversor;
    private final ModuloConversor moduloConversor;
    private final CurrentUserResolver currentUserResolver;

    public List<RolResponse> listAll() {
        return rolConversor.aRolResponseList(rolMapper.listAll());
    }

    public RolResponse findById(UUID id) {
        Rol rol = rolMapper.findById(id);
        if (rol == null) {
            throw new RecursoNoEncontradoException("Rol " + id + " no encontrado");
        }
        return rolConversor.aRolResponse(rol);
    }

    public PageData<RolResponse> listPage(RolQueryParams params) {
        String sortClause = params.resolveSortClause();
        long total = rolMapper.countPaged(
            params.getSearch(), params.getSistema(), params.getEstado());
        List<Rol> items = rolMapper.listPage(
            params.getSearch(), params.getSistema(), params.getEstado(),
            params.getPageSize(), params.offset(), sortClause);
        return PageData.of(
            rolConversor.aRolResponseList(items),
            total,
            params.getPage(),
            params.getPageSize()
        );
    }

    @Transactional
    public RolResponse create(RolCrearRequest request) {
        String codigo = request.codigo().trim().toUpperCase();
        Rol existente = rolMapper.findByCode(codigo);
        if (existente != null) {
            if (Boolean.TRUE.equals(existente.getSistema())) {
                throw new ConflictoNegocioException(
                        "El codigo " + codigo + " esta reservado para un rol del sistema");
            }
            throw new ConflictoNegocioException(
                    "Ya existe un rol con el codigo " + codigo);
        }
        UUID paginaInicioId = validarPaginaInicio(request.paginaInicioId());
        Rol nuevo = Rol.builder()
                .id(UUID.randomUUID())
                .codigo(codigo)
                .nombre(request.nombre())
                .descripcion(request.descripcion())
                .paginaInicioId(paginaInicioId)
                .sistema(false)
                .build();
        rolMapper.insert(nuevo);
        garantizarLandingOtorgado(nuevo.getId(), paginaInicioId);
        return rolConversor.aRolResponse(rolMapper.findById(nuevo.getId()));
    }

    @Transactional
    public RolResponse update(UUID id, RolActualizarRequest request) {
        Rol existente = rolMapper.findById(id);
        if (existente == null) {
            throw new RecursoNoEncontradoException("Rol " + id + " no encontrado");
        }
        if (Boolean.TRUE.equals(existente.getSistema())) {
            throw new ConflictoNegocioException(
                    "El rol " + existente.getCodigo() + " es del sistema y no puede modificarse");
        }
        UUID paginaInicioId = validarPaginaInicio(request.paginaInicioId());
        existente.setNombre(request.nombre());
        existente.setDescripcion(request.descripcion());
        existente.setPaginaInicioId(paginaInicioId);
        rolMapper.update(existente);
        garantizarLandingOtorgado(existente.getId(), paginaInicioId);
        return rolConversor.aRolResponse(rolMapper.findById(existente.getId()));
    }

    @Transactional
    public void delete(UUID id, EliminacionRequest request) {
        Rol existente = rolMapper.findById(id);
        if (existente == null) {
            throw new RecursoNoEncontradoException("Rol " + id + " no encontrado");
        }
        if (Boolean.TRUE.equals(existente.getSistema())) {
            throw new ConflictoNegocioException(
                    "El rol " + existente.getCodigo() + " es del sistema y no puede eliminarse");
        }
        rolMapper.softDelete(id, currentUserResolver.obtenerUsuarioActualId(), request.motivoEliminacion());
    }

    public List<ModuloResponse> listModulesForRole(UUID rolId) {
        Rol rol = rolMapper.findById(rolId);
        if (rol == null) {
            throw new RecursoNoEncontradoException("Rol " + rolId + " no encontrado");
        }
        List<Modulo> modulos = moduloMapper.listByRole(rolId);
        return moduloConversor.aModuloResponseList(modulos);
    }

    @Transactional
    public List<ModuloResponse> replaceModules(UUID rolId, AsignarModulosRolRequest request) {
        Rol rol = rolMapper.findById(rolId);
        if (rol == null) {
            throw new RecursoNoEncontradoException("Rol " + rolId + " no encontrado");
        }
        if (rol.getPaginaInicioId() != null
                && !request.moduloIds().contains(rol.getPaginaInicioId())) {
            throw new ConflictoNegocioException(
                    "No se puede quitar el modulo configurado como pagina de inicio del rol "
                            + rol.getNombre());
        }
        rolModuloMapper.deleteByRole(rolId);
        for (UUID moduloId : request.moduloIds()) {
            if (moduloMapper.findById(moduloId) == null) {
                throw new RecursoNoEncontradoException("Modulo " + moduloId + " no encontrado");
            }
            RolModulo rm = RolModulo.builder()
                    .id(UUID.randomUUID())
                    .rolId(rolId)
                    .moduloId(moduloId)
                    .build();
            rolModuloMapper.insert(rm);
        }
        List<Modulo> modulos = moduloMapper.listByRole(rolId);
        return moduloConversor.aModuloResponseList(modulos);
    }

    private UUID validarPaginaInicio(UUID paginaInicioId) {
        if (paginaInicioId == null) {
            throw new ConflictoNegocioException("El rol debe tener una pagina de inicio configurada");
        }
        Modulo modulo = moduloMapper.findById(paginaInicioId);
        if (modulo == null) {
            throw new RecursoNoEncontradoException(
                    "Modulo " + paginaInicioId + " no encontrado");
        }
        if (modulo.getEstado() == null || modulo.getEstado() != 1) {
            throw new ConflictoNegocioException(
                    "El modulo " + modulo.getNombre() + " esta deshabilitado y no puede ser pagina de inicio");
        }
        return paginaInicioId;
    }

    private void garantizarLandingOtorgado(UUID rolId, UUID paginaInicioId) {
        if (rolModuloMapper.count(rolId, paginaInicioId) == 0) {
            RolModulo rm = RolModulo.builder()
                    .id(UUID.randomUUID())
                    .rolId(rolId)
                    .moduloId(paginaInicioId)
                    .build();
            rolModuloMapper.insert(rm);
        }
    }
}