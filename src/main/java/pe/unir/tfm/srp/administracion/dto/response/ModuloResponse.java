package pe.unir.tfm.srp.administracion.dto.response;

import java.util.UUID;

public record ModuloResponse(
    UUID id,
    String codigo,
    String nombre,
    String ruta,
    String icono,
    Short orden,
    UUID moduloPadreId,
    String descripcion,
    Short estado
) {}
