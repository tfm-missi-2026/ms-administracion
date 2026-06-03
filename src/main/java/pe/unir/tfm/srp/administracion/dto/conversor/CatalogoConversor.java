package pe.unir.tfm.srp.administracion.dto.conversor;

import java.util.List;

import org.mapstruct.Mapper;

import pe.unir.tfm.srp.administracion.dto.response.CatalogoResponse;
import pe.unir.tfm.srp.administracion.model.Catalogo;

@Mapper
public interface CatalogoConversor {

    CatalogoResponse aCatalogoResponse(Catalogo catalogo);

    List<CatalogoResponse> aCatalogoResponseList(List<Catalogo> catalogos);
}
