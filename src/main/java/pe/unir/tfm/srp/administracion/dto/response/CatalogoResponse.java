package pe.unir.tfm.srp.administracion.dto.response;

import java.util.UUID;

public record CatalogoResponse(
    UUID id,
    String grupo,
    Short idOpcion,
    String opcion,
    Short estado
) {}
