package pe.unir.tfm.srp.administracion.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CambiarContraseniaRequest(
    @NotBlank String contraseniaActual,
    @NotBlank @Size(min = 8, max = 100) String contraseniaNueva
) {}
