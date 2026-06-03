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
public class Modulo {
    private UUID id;
    private String codigo;
    private String nombre;
    private String ruta;
    private String icono;
    private Short orden;
    private UUID moduloPadreId;
    private String descripcion;
    private Short estado;
    private LocalDateTime fechaCreacion;
    private UUID usuarioCreacion;
    private LocalDateTime fechaModificacion;
    private UUID usuarioModificacion;
    private LocalDateTime fechaEliminacion;
    private UUID usuarioEliminacion;
    private String motivoEliminacion;
}
