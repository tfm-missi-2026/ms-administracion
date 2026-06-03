package pe.unir.tfm.srp.administracion.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pe.unir.tfm.srp.administracion.dto.conversor.UsuarioConversor;
import pe.unir.tfm.srp.administracion.dto.request.LoginRequest;
import pe.unir.tfm.srp.administracion.dto.response.LoginResponse;
import pe.unir.tfm.srp.administracion.model.Usuario;
import pe.unir.tfm.srp.administracion.repository.UsuarioMapper;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UsuarioConversor usuarioConversor;

    public LoginResponse autenticar(LoginRequest request) {
        Usuario usuario = usuarioMapper.buscarPorEmail(request.email());
        if (usuario == null) {
            throw new BadCredentialsException("Credenciales invalidas");
        }
        if (usuario.getEstado() == null || usuario.getEstado() == 0) {
            throw new DisabledException("Usuario deshabilitado");
        }
        if (!passwordEncoder.matches(request.contrasenia(), usuario.getContrasenia())) {
            throw new BadCredentialsException("Credenciales invalidas");
        }

        JwtService.TokenGenerado token = jwtService.generarToken(usuario);
        return new LoginResponse(
                token.token(),
                "Bearer",
                token.expiraEnSegundos(),
                usuarioConversor.aUsuarioInfo(usuario));
    }
}
