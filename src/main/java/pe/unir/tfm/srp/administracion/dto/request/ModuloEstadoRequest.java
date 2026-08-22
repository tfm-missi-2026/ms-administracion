package pe.unir.tfm.srp.administracion.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Cambia el estado de un modulo (0 = deshabilitado, 1 = habilitado).
 * `motivoEliminacion` es obligatorio cuando se deshabilita (estado=0);
 * cuando se rehabilita (estado=1) es opcional.
 */
public record ModuloEstadoRequest(
    @NotNull Short estado,
    @Size(max = 500) String motivoEliminacion
) {}