package pe.unir.tfm.srp.administracion.dto.request;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record AsignarModulosRolRequest(
    @NotNull List<UUID> moduloIds
) {}
