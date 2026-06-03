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
public class Catalogo {
    private UUID id;
    private String grupo;
    private Short idOpcion;
    private String opcion;
    private Short estado;
    private LocalDateTime fechaCreacion;
    private UUID usuarioCreacion;
    private LocalDateTime fechaModificacion;
    private UUID usuarioModificacion;
    private LocalDateTime fechaEliminacion;
    private UUID usuarioEliminacion;
    private String motivoEliminacion;
}
