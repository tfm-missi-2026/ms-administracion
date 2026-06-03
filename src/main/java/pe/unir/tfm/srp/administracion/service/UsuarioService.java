package pe.unir.tfm.srp.administracion.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.unir.tfm.srp.administracion.dto.conversor.ModuloConversor;
import pe.unir.tfm.srp.administracion.dto.conversor.UsuarioConversor;
import pe.unir.tfm.srp.administracion.dto.request.EliminacionRequest;
import pe.unir.tfm.srp.administracion.dto.request.UsuarioActualizarRequest;
import pe.unir.tfm.srp.administracion.dto.request.UsuarioCrearRequest;
import pe.unir.tfm.srp.administracion.dto.response.ModuloResponse;
import pe.unir.tfm.srp.administracion.dto.response.UsuarioActualResponse;
import pe.unir.tfm.srp.administracion.dto.response.UsuarioResponse;
import pe.unir.tfm.srp.administracion.config.CurrentUserResolver;
import pe.unir.tfm.srp.administracion.exception.ConflictoNegocioException;
import pe.unir.tfm.srp.administracion.exception.RecursoNoEncontradoException;
import pe.unir.tfm.srp.administracion.model.Modulo;
import pe.unir.tfm.srp.administracion.model.Rol;
import pe.unir.tfm.srp.administracion.model.Usuario;
import pe.unir.tfm.srp.administracion.repository.ModuloMapper;
import pe.unir.tfm.srp.administracion.repository.RolMapper;
import pe.unir.tfm.srp.administracion.repository.UsuarioMapper;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioMapper usuarioMapper;
    private final RolMapper rolMapper;
    private final ModuloMapper moduloMapper;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioConversor usuarioConversor;
    private final ModuloConversor moduloConversor;
    private final CurrentUserResolver currentUserResolver;

    public List<UsuarioResponse> listar() {
        return usuarioConversor.aUsuarioResponseList(usuarioMapper.listarActivos());
    }

    public UsuarioResponse buscarPorId(UUID id) {
        Usuario usuario = usuarioMapper.buscarPorId(id);
        if (usuario == null) {
            throw new RecursoNoEncontradoException("Usuario " + id + " no encontrado");
        }
        return usuarioConversor.aUsuarioResponse(usuario);
    }

    public UsuarioActualResponse obtenerUsuarioActual(UUID usuarioId) {
        Usuario usuario = usuarioMapper.buscarPorId(usuarioId);
        if (usuario == null) {
            throw new RecursoNoEncontradoException("Usuario " + usuarioId + " no encontrado");
        }
        List<Modulo> modulos = moduloMapper.listarPorRol(usuario.getRolId());
        List<ModuloResponse> modulosResponse = moduloConversor.aModuloResponseList(modulos);
        return usuarioConversor.aUsuarioActual(usuario, modulosResponse);
    }

    @Transactional
    public UsuarioResponse crear(UsuarioCrearRequest request) {
        if (usuarioMapper.contarPorEmail(request.email()) > 0) {
            throw new ConflictoNegocioException("Ya existe un usuario con el email " + request.email());
        }
        Rol rol = rolMapper.buscarPorId(request.rolId());
        if (rol == null) {
            throw new RecursoNoEncontradoException("Rol " + request.rolId() + " no encontrado");
        }

        Usuario nuevo = Usuario.builder()
                .id(UUID.randomUUID())
                .email(request.email())
                .contrasenia(passwordEncoder.encode(request.contrasenia()))
                .nombres(request.nombres())
                .apellidoPaterno(request.apellidoPaterno())
                .apellidoMaterno(request.apellidoMaterno())
                .rolId(request.rolId())
                .build();
        usuarioMapper.insertar(nuevo);
        nuevo.setRol(rol);
        return usuarioConversor.aUsuarioResponse(nuevo);
    }

    @Transactional
    public UsuarioResponse actualizar(UUID id, UsuarioActualizarRequest request) {
        Usuario existente = usuarioMapper.buscarPorId(id);
        if (existente == null) {
            throw new RecursoNoEncontradoException("Usuario " + id + " no encontrado");
        }
        Rol rol = rolMapper.buscarPorId(request.rolId());
        if (rol == null) {
            throw new RecursoNoEncontradoException("Rol " + request.rolId() + " no encontrado");
        }
        existente.setEmail(request.email());
        existente.setNombres(request.nombres());
        existente.setApellidoPaterno(request.apellidoPaterno());
        existente.setApellidoMaterno(request.apellidoMaterno());
        existente.setRolId(request.rolId());
        existente.setEstado(request.estado());
        usuarioMapper.actualizar(existente);
        existente.setRol(rol);
        return usuarioConversor.aUsuarioResponse(existente);
    }

    @Transactional
    public void eliminar(UUID id, EliminacionRequest request) {
        Usuario existente = usuarioMapper.buscarPorId(id);
        if (existente == null) {
            throw new RecursoNoEncontradoException("Usuario " + id + " no encontrado");
        }
        usuarioMapper.eliminarLogico(id, currentUserResolver.obtenerUsuarioActualId(), request.motivoEliminacion());
    }
}
