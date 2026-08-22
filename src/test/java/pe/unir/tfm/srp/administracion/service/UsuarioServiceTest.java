package pe.unir.tfm.srp.administracion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import pe.unir.tfm.srp.administracion.config.CurrentUserResolver;
import pe.unir.tfm.srp.administracion.dto.conversor.ModuloConversor;
import pe.unir.tfm.srp.administracion.dto.conversor.UsuarioConversor;
import pe.unir.tfm.srp.administracion.dto.request.EliminacionRequest;
import pe.unir.tfm.srp.administracion.dto.request.UsuarioActualizarRequest;
import pe.unir.tfm.srp.administracion.dto.request.UsuarioCrearRequest;
import pe.unir.tfm.srp.administracion.dto.response.UsuarioResponse;
import pe.unir.tfm.srp.administracion.exception.ConflictoNegocioException;
import pe.unir.tfm.srp.administracion.exception.RecursoNoEncontradoException;
import pe.unir.tfm.srp.administracion.model.Rol;
import pe.unir.tfm.srp.administracion.model.Usuario;
import pe.unir.tfm.srp.administracion.repository.ModuloMapper;
import pe.unir.tfm.srp.administracion.repository.RolMapper;
import pe.unir.tfm.srp.administracion.repository.UsuarioMapper;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    private static final UUID ID = UUID.fromString("00000000-0000-0000-0000-000000000900");
    private static final UUID ROL_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Mock private UsuarioMapper usuarioMapper;
    @Mock private RolMapper rolMapper;
    @Mock private ModuloMapper moduloMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UsuarioConversor usuarioConversor;
    @Mock private ModuloConversor moduloConversor;
    @Mock private CurrentUserResolver currentUserResolver;

    @InjectMocks private UsuarioService usuarioService;

    private Rol rolAdmin() {
        return Rol.builder().id(ROL_ID).codigo("ADMIN").nombre("Administrador").build();
    }

    private Usuario usuarioActivo() {
        return Usuario.builder()
                .id(ID)
                .email("admin@srp.local")
                .nombres("Admin")
                .apellidoPaterno("del")
                .apellidoMaterno("Sistema")
                .rolId(ROL_ID)
                .estado((short) 1)
                .build();
    }

    private UsuarioCrearRequest crearRequest() {
        return new UsuarioCrearRequest(
                "nuevo@srp.local", "Clave123", "Nuevo", "Apellido", "Materno", ROL_ID);
    }

    @Test
    void create_emailDuplicado_lanzaConflictoYNoInserta() {
        when(usuarioMapper.countByEmail("nuevo@srp.local")).thenReturn(1);

        assertThatThrownBy(() -> usuarioService.create(crearRequest()))
                .isInstanceOf(ConflictoNegocioException.class)
                .hasMessageContaining("Ya existe un usuario");

        verify(usuarioMapper, never()).insert(any());
    }

    @Test
    void create_rolNoExiste_lanzaRecursoNoEncontrado() {
        when(usuarioMapper.countByEmail("nuevo@srp.local")).thenReturn(0);
        when(rolMapper.findById(ROL_ID)).thenReturn(null);

        assertThatThrownBy(() -> usuarioService.create(crearRequest()))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void create_datosValidos_hasheaContraseniaEInserta() {
        when(usuarioMapper.countByEmail("nuevo@srp.local")).thenReturn(0);
        when(rolMapper.findById(ROL_ID)).thenReturn(rolAdmin());
        when(passwordEncoder.encode("Clave123")).thenReturn("$2a$10$hash");
        UsuarioResponse esperado = new UsuarioResponse(
                UUID.randomUUID(), "nuevo@srp.local", "Nuevo", "Apellido", "Materno",
                null, (short) 1);
        when(usuarioConversor.aUsuarioResponse(any(Usuario.class))).thenReturn(esperado);

        UsuarioResponse resultado = usuarioService.create(crearRequest());

        assertThat(resultado.email()).isEqualTo("nuevo@srp.local");
        verify(passwordEncoder).encode("Clave123");
        verify(usuarioMapper).insert(any(Usuario.class));
    }

    @Test
    void findById_noExiste_lanzaRecursoNoEncontrado() {
        when(usuarioMapper.findById(ID)).thenReturn(null);

        assertThatThrownBy(() -> usuarioService.findById(ID))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void delete_usuarioNoExiste_lanzaRecursoNoEncontrado() {
        when(usuarioMapper.findById(ID)).thenReturn(null);

        assertThatThrownBy(() -> usuarioService.delete(
                ID, new EliminacionRequest("Baja")))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void delete_usuarioExiste_usaUsuarioActualYEliminaLogico() {
        when(usuarioMapper.findById(ID)).thenReturn(usuarioActivo());
        UUID currentUser = UUID.fromString("00000000-0000-0000-0000-000000000099");
        when(currentUserResolver.obtenerUsuarioActualId()).thenReturn(currentUser);

        usuarioService.delete(ID, new EliminacionRequest("Renuncia"));

        verify(usuarioMapper).softDelete(eq(ID), eq(currentUser), eq("Renuncia"));
    }

    @Test
    void update_usuarioNoExiste_lanzaRecursoNoEncontrado() {
        when(usuarioMapper.findById(ID)).thenReturn(null);

        UsuarioActualizarRequest req = new UsuarioActualizarRequest(
                "nuevo@srp.local", "Nuevo", "Apellido", "Materno", ROL_ID);

        assertThatThrownBy(() -> usuarioService.update(ID, req))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void list_delegaMappersYConversor() {
        Usuario u = usuarioActivo();
        when(usuarioMapper.listActive()).thenReturn(List.of(u));
        when(usuarioConversor.aUsuarioResponseList(List.of(u))).thenReturn(List.of());

        usuarioService.list();

        verify(usuarioMapper).listActive();
        verify(usuarioConversor).aUsuarioResponseList(List.of(u));
    }
}