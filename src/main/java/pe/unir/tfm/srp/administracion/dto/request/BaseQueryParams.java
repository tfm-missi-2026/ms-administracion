package pe.unir.tfm.srp.administracion.dto.request;

import java.util.Map;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

/**
 * Parametros base de cualquier listado paginado del backend.
 * Subclases (ej. {@link ModuloQueryParams}, {@link RolQueryParams}) extienden
 * y agregan los filtros de dominio especificos del recurso.
 *
 * Spring bindea los query params de la URL a las propiedades de esta
 * clase automaticamente al declarar el parametro en la firma del controller:
 *
 *   @GetMapping
 *   public PageResponse<X> listar(@Valid ModuloQueryParams params) { ... }
 *
 * El fragmento ORDER BY se construye SIEMPRE desde la whitelist de
 * {@link #sortableColumns()}, nunca del input del usuario. Protege contra
 * SQL injection al usar `${sortClause}` en los mappers XML.
 */
@Getter
@Setter
public abstract class BaseQueryParams {

    @Min(1)
    private Integer page = 1;

    @Min(1)
    @Max(100)
    private Integer pageSize = 10;

    private String search;
    private String sortBy;
    private String sortDir = "asc";

    /**
     * Whitelist de columnas ordenables: "alias frontend" -> "columna SQL".
     * Solo estas se aceptan en ORDER BY.
     */
    protected abstract Map<String, String> sortableColumns();

    public long offset() {
        return (long) (page - 1) * pageSize;
    }

    /**
     * Devuelve el fragmento ORDER BY seguro (columna + direccion), o null
     * si no hay sort valido o la columna no esta en la whitelist.
     */
    public String resolveSortClause() {
        if (sortBy == null || sortBy.isBlank()) return null;
        String col = sortableColumns().get(sortBy);
        if (col == null) return null;
        String dir = "desc".equalsIgnoreCase(sortDir) ? "DESC" : "ASC";
        return col + " " + dir;
    }
}