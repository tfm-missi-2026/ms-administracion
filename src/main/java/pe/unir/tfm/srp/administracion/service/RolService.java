package pe.unir.tfm.srp.administracion.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.unir.tfm.srp.administracion.dto.conversor.ModuloConversor;
import pe.unir.tfm.srp.administracion.dto.conversor.RolConversor;
import pe.unir.tfm.srp.administracion.dto.request.AsignarModulosRolRequest;
import pe.unir.tfm.srp.administracion.dto.request.EliminacionRequest;
import pe.unir.tfm.srp.administracion.dto.request.RolActualizarRequest;
import pe.unir.tfm.srp.administracion.dto.request.RolCrearRequest;
import pe.unir.tfm.srp.administracion.dto.response.ModuloResponse;
import pe.unir.tfm.srp.administracion.dto.response.RolResponse;
import pe.unir.tfm.srp.administracion.config.CurrentUserResolver;
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

    public List<RolResponse> listar() {
        return rolConversor.aRolResponseList(rolMapper.listarActivos());
    }

    public RolResponse buscarPorId(UUID id) {
        Rol rol = rolMapper.buscarPorId(id);
        if (rol == null) {
            throw new RecursoNoEncontradoException("Rol " + id + " no encontrado");
        }
        return rolConversor.aRolResponse(rol);
    }

    @Transactional
    public RolResponse crear(RolCrearRequest request) {
        if (rolMapper.buscarPorCodigo(request.codigo()) != null) {
            throw new ConflictoNegocioException("Ya existe un rol con el codigo " + request.codigo());
        }
        Rol nuevo = Rol.builder()
                .id(UUID.randomUUID())
                .codigo(request.codigo())
                .nombre(request.nombre())
                .descripcion(request.descripcion())
                .build();
        rolMapper.insertar(nuevo);
        return rolConversor.aRolResponse(nuevo);
    }

    @Transactional
    public RolResponse actualizar(UUID id, RolActualizarRequest request) {
        Rol existente = rolMapper.buscarPorId(id);
        if (existente == null) {
            throw new RecursoNoEncontradoException("Rol " + id + " no encontrado");
        }
        existente.setNombre(request.nombre());
        existente.setDescripcion(request.descripcion());
        rolMapper.actualizar(existente);
        return rolConversor.aRolResponse(existente);
    }

    @Transactional
    public void eliminar(UUID id, EliminacionRequest request) {
        Rol existente = rolMapper.buscarPorId(id);
        if (existente == null) {
            throw new RecursoNoEncontradoException("Rol " + id + " no encontrado");
        }
        rolMapper.eliminarLogico(id, currentUserResolver.obtenerUsuarioActualId(), request.motivoEliminacion());
    }

    public List<ModuloResponse> listarModulosDelRol(UUID rolId) {
        Rol rol = rolMapper.buscarPorId(rolId);
        if (rol == null) {
            throw new RecursoNoEncontradoException("Rol " + rolId + " no encontrado");
        }
        List<Modulo> modulos = moduloMapper.listarPorRol(rolId);
        return moduloConversor.aModuloResponseList(modulos);
    }

    @Transactional
    public List<ModuloResponse> reemplazarModulosDelRol(UUID rolId, AsignarModulosRolRequest request) {
        Rol rol = rolMapper.buscarPorId(rolId);
        if (rol == null) {
            throw new RecursoNoEncontradoException("Rol " + rolId + " no encontrado");
        }
        rolModuloMapper.eliminarFisicoPorRol(rolId);
        for (UUID moduloId : request.moduloIds()) {
            if (moduloMapper.buscarPorId(moduloId) == null) {
                throw new RecursoNoEncontradoException("Modulo " + moduloId + " no encontrado");
            }
            RolModulo rm = RolModulo.builder()
                    .id(UUID.randomUUID())
                    .rolId(rolId)
                    .moduloId(moduloId)
                    .build();
            rolModuloMapper.insertar(rm);
        }
        List<Modulo> modulos = moduloMapper.listarPorRol(rolId);
        return moduloConversor.aModuloResponseList(modulos);
    }
}
