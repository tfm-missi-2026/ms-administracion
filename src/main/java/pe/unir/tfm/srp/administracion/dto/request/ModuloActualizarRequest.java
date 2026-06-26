package pe.unir.tfm.srp.administracion.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ModuloActualizarRequest(
    @NotBlank @Size(max = 100) String nombre,
    @NotBlank @Size(max = 150) String ruta,
    @Size(max = 50) String icono,
    @NotNull Short orden,
    UUID moduloPadreId,
    String descripcion
) {}
