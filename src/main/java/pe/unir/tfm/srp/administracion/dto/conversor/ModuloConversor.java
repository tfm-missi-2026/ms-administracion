package pe.unir.tfm.srp.administracion.dto.conversor;

import java.util.List;

import org.mapstruct.Mapper;

import pe.unir.tfm.srp.administracion.dto.response.ModuloResponse;
import pe.unir.tfm.srp.administracion.model.Modulo;

@Mapper
public interface ModuloConversor {

    ModuloResponse aModuloResponse(Modulo modulo);

    List<ModuloResponse> aModuloResponseList(List<Modulo> modulos);
}
