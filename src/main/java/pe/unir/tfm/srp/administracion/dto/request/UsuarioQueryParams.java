package pe.unir.tfm.srp.administracion.dto.request;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * Filtros especificos del listado paginado de usuarios. Hereda
 * paginacion, search y sort de {@link BaseQueryParams} y agrega los
 * filtros de dominio:
 *   - rolCodigo: ADMIN | JEFE_AREA | GESTOR_PROYECTO | RECURSO_TECNICO
 *   - estado: 0 (deshabilitado) | 1 (habilitado) | null (todos)
 *
 * Las columnas ordenables se mapean a su columna SQL real en la whitelist.
 */
@Getter
@Setter
public class UsuarioQueryParams extends BaseQueryParams {

    private String rolCodigo;
    private Integer estado;

    @Override
    protected Map<String, String> sortableColumns() {
        return Map.of(
            "email", "u.email",
            "nombres", "u.nombres",
            "apellidoPaterno", "u.apellido_paterno",
            "apellidoMaterno", "u.apellido_materno",
            "rol", "r.nombre",
            "estado", "u.estado"
        );
    }
}
