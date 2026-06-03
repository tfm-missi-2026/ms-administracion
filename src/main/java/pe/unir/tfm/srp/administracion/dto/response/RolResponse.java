package pe.unir.tfm.srp.administracion.dto.response;

import java.util.UUID;

public record RolResponse(
    UUID id,
    String codigo,
    String nombre,
    String descripcion,
    Short estado
) {}
