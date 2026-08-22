package pe.unir.tfm.srp.administracion.dto.request;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * Filtros especificos del listado de modulos. Hereda paginacion, search
 * y sort de {@link BaseQueryParams} y agrega filtros de dominio:
 *   - seccion: codigo de la seccion (OPERACION, SEGUIMIENTO, ...)
 *   - estado: 0 (deshabilitado) | 1 (habilitado) | null (todos)
 *
 * Las columnas ordenables se mapean a su columna SQL real en la whitelist.
 */
@Getter
@Setter
public class ModuloQueryParams extends BaseQueryParams {

    private String seccion;
    private Integer estado;

    @Override
    protected Map<String, String> sortableColumns() {
        return Map.of(
            "codigo", "m.codigo",
            "nombre", "m.nombre",
            "seccion", "s.orden",
            "orden", "m.orden",
            "estado", "m.estado"
        );
    }
}