package pe.unir.tfm.srp.administracion.dto.response;

import java.util.List;
import java.util.UUID;

public record UsuarioActualResponse(
    UUID id,
    String email,
    String nombres,
    String apellidoPaterno,
    String apellidoMaterno,
    RolResponse rol,
    List<ModuloResponse> modulos
) {}
