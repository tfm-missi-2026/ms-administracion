package pe.unir.tfm.srp.administracion.dto.response;

import java.util.UUID;

public record UsuarioInfoResponse(
    UUID id,
    String email,
    String nombreCompleto,
    RolResponse rol
) {}
