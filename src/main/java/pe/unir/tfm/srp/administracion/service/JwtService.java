package pe.unir.tfm.srp.administracion.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import pe.unir.tfm.srp.administracion.model.Usuario;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-minutes:60}")
    private long expiracionMinutos;

    public TokenGenerado generarToken(Usuario usuario) {
        try {
            Instant emitido = Instant.now();
            Instant expira = emitido.plus(expiracionMinutos, ChronoUnit.MINUTES);

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(usuario.getId().toString())
                    .claim("email", usuario.getEmail())
                    .claim("rol", usuario.getRol().getCodigo())
                    .issueTime(Date.from(emitido))
                    .expirationTime(Date.from(expira))
                    .build();

            SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            jwt.sign(new MACSigner(new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256")));

            return new TokenGenerado(jwt.serialize(), expiracionMinutos * 60);
        } catch (JOSEException ex) {
            throw new IllegalStateException("No se pudo firmar el JWT", ex);
        }
    }

    public record TokenGenerado(String token, long expiraEnSegundos) {}
}
