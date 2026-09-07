package pe.unir.tfm.srp.administracion.dto.request;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CatalogoQueryParams extends BaseQueryParams {

    private String grupo;
    private Integer estado;

    @Override
    protected Map<String, String> sortableColumns() {
        return Map.of(
            "code", "c.id_opcion",
            "name", "c.opcion",
            "order", "c.id_opcion",
            "status", "c.estado"
        );
    }
}
