package pe.unir.tfm.srp.administracion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;

import pe.unir.tfm.srp.administracion.dto.conversor.UsuarioConversor;
import pe.unir.tfm.srp.administracion.dto.request.LoginRequest;
import pe.unir.tfm.srp.administracion.dto.response.LoginResponse;
import pe.unir.tfm.srp.administracion.dto.response.UsuarioInfoResponse;
import pe.unir.tfm.srp.administracion.model.Usuario;
import pe.unir.tfm.srp.administracion.model.Rol;
import pe.unir.tfm.srp.administracion.repository.UsuarioMapper;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String EMAIL = "admin@srp.local";
    private static final String PASSWORD = "Admin123";
    private static final String HASH = "$2a$10$abcdefghijklmnopqrstuv";

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private UsuarioConversor usuarioConversor;

    @InjectMocks
    private AuthService authService;

    private Usuario usuarioActivo() {
        return Usuario.builder()
                .email(EMAIL)
                .contrasenia(HASH)
                .estado((short) 1)
                .rol(Rol.builder().codigo("ADMIN").nombre("Administrador").build())
                .build();
    }

    private LoginRequest request() {
        return new LoginRequest(EMAIL, PASSWORD);
    }

    @Test
    void autenticar_emailNoExiste_lanzaBadCredentials() {
        when(usuarioMapper.findByEmail(EMAIL)).thenReturn(null);

        assertThatThrownBy(() -> authService.autenticar(request()))
                .isInstanceOf(BadCredentialsException.class);

        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtService, never()).generarToken(any());
    }

    @Test
    void autenticar_usuarioDeshabilitado_lanzaDisabled() {
        Usuario deshabilitado = usuarioActivo();
        deshabilitado.setEstado((short) 0);
        when(usuarioMapper.findByEmail(EMAIL)).thenReturn(deshabilitado);

        assertThatThrownBy(() -> authService.autenticar(request()))
                .isInstanceOf(DisabledException.class);
    }

    @Test
    void autenticar_contraseniaIncorrecta_lanzaBadCredentials() {
        when(usuarioMapper.findByEmail(EMAIL)).thenReturn(usuarioActivo());
        when(passwordEncoder.matches(PASSWORD, HASH)).thenReturn(false);

        assertThatThrownBy(() -> authService.autenticar(request()))
                .isInstanceOf(BadCredentialsException.class);

        verify(jwtService, never()).generarToken(any());
    }

    @Test
    void autenticar_credencialesValidas_devuelveLoginResponse() {
        Usuario usuario = usuarioActivo();
        when(usuarioMapper.findByEmail(EMAIL)).thenReturn(usuario);
        when(passwordEncoder.matches(PASSWORD, HASH)).thenReturn(true);
        when(jwtService.generarToken(usuario))
                .thenReturn(new JwtService.TokenGenerado("jwt.token", 3600L));
        UsuarioInfoResponse info = new UsuarioInfoResponse(
                java.util.UUID.randomUUID(), EMAIL, "Administrador del Sistema", null);
        when(usuarioConversor.aUsuarioInfo(usuario)).thenReturn(info);

        LoginResponse respuesta = authService.autenticar(request());

        assertThat(respuesta.tokenAcceso()).isEqualTo("jwt.token");
        assertThat(respuesta.tipoToken()).isEqualTo("Bearer");
        assertThat(respuesta.expiraEnSegundos()).isEqualTo(3600L);
        assertThat(respuesta.usuario()).isEqualTo(info);
    }
}
