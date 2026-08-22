package pe.unir.tfm.srp.administracion.dto.response;

import java.util.UUID;

public record ModuloResponse(
    UUID id,
    String codigo,
    String nombre,
    String icono,
    Short orden,
    UUID moduloPadreId,
    String seccion,
    String tipo,
    String seccionTitulo,
    Integer seccionOrden,
    Boolean sistema,
    String descripcion,
    Short estado
) {}