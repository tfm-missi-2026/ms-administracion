package pe.unir.tfm.srp.administracion.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioActualizarRequest(
    @NotBlank @Email @Size(max = 150) String email,
    @NotBlank @Size(max = 100) String nombres,
    @NotBlank @Size(max = 100) String apellidoPaterno,
    @NotBlank @Size(max = 100) String apellidoMaterno,
    @NotNull UUID rolId
) {}
