package pe.unir.tfm.srp.administracion.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    private UUID id;
    private String email;
    private String contrasenia;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private UUID rolId;
    private Short estado;
    private LocalDateTime fechaCreacion;
    private UUID usuarioCreacion;
    private LocalDateTime fechaModificacion;
    private UUID usuarioModificacion;
    private LocalDateTime fechaEliminacion;
    private UUID usuarioEliminacion;
    private String motivoEliminacion;

    private Rol rol;
}
