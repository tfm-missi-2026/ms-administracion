package pe.unir.tfm.srp.administracion.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CatalogoActualizarRequest(
    @NotBlank @Size(max = 50) String grupo,
    @NotNull Short idOpcion,
    @NotBlank @Size(max = 150) String opcion,
    @NotNull Short estado
) {}
