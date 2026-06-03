package pe.unir.tfm.srp.administracion.dto.response;

public record LoginResponse(
    String tokenAcceso,
    String tipoToken,
    long expiraEnSegundos,
    UsuarioInfoResponse usuario
) {}
