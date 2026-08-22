package pe.unir.tfm.srp.administracion.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ModuloActualizarRequest(
    @NotBlank @Size(max = 100) String nombre,
    @Size(max = 50) String icono,
    @NotNull Short orden,
    @NotBlank @Size(max = 40) String seccion,
    @NotBlank @Size(max = 20) String tipo,
    String descripcion
) {}