package pe.unir.tfm.srp.administracion.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
    @NotBlank @Size(min = 8, max = 100) String contrasenia
) {}
