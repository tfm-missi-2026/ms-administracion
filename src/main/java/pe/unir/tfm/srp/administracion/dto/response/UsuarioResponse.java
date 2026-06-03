package pe.unir.tfm.srp.administracion.dto.response;

import java.util.UUID;

public record UsuarioResponse(
    UUID id,
    String email,
    String nombres,
    String apellidoPaterno,
    String apellidoMaterno,
    RolResponse rol,
    Short estado
) {}
