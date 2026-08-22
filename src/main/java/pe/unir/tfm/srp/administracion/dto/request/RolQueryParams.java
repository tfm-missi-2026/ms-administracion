package pe.unir.tfm.srp.administracion.dto.request;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * Filtros especificos del listado de roles. Hereda paginacion, search y
 * sort de {@link BaseQueryParams} y agrega filtros de dominio:
 *   - sistema: true (rol del sistema) | false (custom) | null (todos)
 *   - estado: 0 (deshabilitado) | 1 (habilitado) | null (todos)
 */
@Getter
@Setter
public class RolQueryParams extends BaseQueryParams {

    private Boolean sistema;
    private Integer estado;

    @Override
    protected Map<String, String> sortableColumns() {
        return Map.of(
            "codigo", "codigo",
            "nombre", "nombre",
            "sistema", "sistema",
            "estado", "estado"
        );
    }
}