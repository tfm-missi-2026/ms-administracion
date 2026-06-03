package pe.unir.tfm.srp.administracion.dto.conversor;

import java.util.List;

import org.mapstruct.Mapper;

import pe.unir.tfm.srp.administracion.dto.response.RolResponse;
import pe.unir.tfm.srp.administracion.model.Rol;

@Mapper
public interface RolConversor {

    RolResponse aRolResponse(Rol rol);

    List<RolResponse> aRolResponseList(List<Rol> roles);
}
