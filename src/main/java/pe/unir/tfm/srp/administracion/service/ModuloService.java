package pe.unir.tfm.srp.administracion.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.unir.tfm.srp.administracion.dto.conversor.ModuloConversor;
import pe.unir.tfm.srp.administracion.dto.request.EliminacionRequest;
import pe.unir.tfm.srp.administracion.dto.request.ModuloActualizarRequest;
import pe.unir.tfm.srp.administracion.dto.request.ModuloCrearRequest;
import pe.unir.tfm.srp.administracion.dto.response.ModuloResponse;
import pe.unir.tfm.srp.administracion.config.CurrentUserResolver;
import pe.unir.tfm.srp.administracion.exception.ConflictoNegocioException;
import pe.unir.tfm.srp.administracion.exception.RecursoNoEncontradoException;
import pe.unir.tfm.srp.administracion.model.Modulo;
import pe.unir.tfm.srp.administracion.repository.ModuloMapper;

@Service
@RequiredArgsConstructor
public class ModuloService {

    private final ModuloMapper moduloMapper;
    private final ModuloConversor moduloConversor;
    private final CurrentUserResolver currentUserResolver;

    public List<ModuloResponse> listar() {
        return moduloConversor.aModuloResponseList(moduloMapper.listarActivos());
    }

    public ModuloResponse buscarPorId(UUID id) {
        Modulo modulo = moduloMapper.buscarPorId(id);
        if (modulo == null) {
            throw new RecursoNoEncontradoException("Modulo " + id + " no encontrado");
        }
        return moduloConversor.aModuloResponse(modulo);
    }

    public List<ModuloResponse> listarPorRol(UUID rolId) {
        return moduloConversor.aModuloResponseList(moduloMapper.listarPorRol(rolId));
    }

    @Transactional
    public ModuloResponse crear(ModuloCrearRequest request) {
        if (moduloMapper.buscarPorCodigo(request.codigo()) != null) {
            throw new ConflictoNegocioException("Ya existe un modulo con el codigo " + request.codigo());
        }
        if (request.moduloPadreId() != null && moduloMapper.buscarPorId(request.moduloPadreId()) == null) {
            throw new RecursoNoEncontradoException("Modulo padre " + request.moduloPadreId() + " no encontrado");
        }
        Modulo nuevo = Modulo.builder()
                .id(UUID.randomUUID())
                .codigo(request.codigo())
                .nombre(request.nombre())
                .ruta(request.ruta())
                .icono(request.icono())
                .orden(request.orden())
                .moduloPadreId(request.moduloPadreId())
                .descripcion(request.descripcion())
                .build();
        moduloMapper.insertar(nuevo);
        return moduloConversor.aModuloResponse(nuevo);
    }

    @Transactional
    public ModuloResponse actualizar(UUID id, ModuloActualizarRequest request) {
        Modulo existente = moduloMapper.buscarPorId(id);
        if (existente == null) {
            throw new RecursoNoEncontradoException("Modulo " + id + " no encontrado");
        }
        if (request.moduloPadreId() != null && moduloMapper.buscarPorId(request.moduloPadreId()) == null) {
            throw new RecursoNoEncontradoException("Modulo padre " + request.moduloPadreId() + " no encontrado");
        }
        existente.setNombre(request.nombre());
        existente.setRuta(request.ruta());
        existente.setIcono(request.icono());
        existente.setOrden(request.orden());
        existente.setModuloPadreId(request.moduloPadreId());
        existente.setDescripcion(request.descripcion());
        moduloMapper.actualizar(existente);
        return moduloConversor.aModuloResponse(existente);
    }

    @Transactional
    public void eliminar(UUID id, EliminacionRequest request) {
        Modulo existente = moduloMapper.buscarPorId(id);
        if (existente == null) {
            throw new RecursoNoEncontradoException("Modulo " + id + " no encontrado");
        }
        moduloMapper.eliminarLogico(id, currentUserResolver.obtenerUsuarioActualId(), request.motivoEliminacion());
    }
}
