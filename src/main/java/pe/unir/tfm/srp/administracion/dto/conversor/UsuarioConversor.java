package pe.unir.tfm.srp.administracion.dto.conversor;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import pe.unir.tfm.srp.administracion.dto.response.ModuloResponse;
import pe.unir.tfm.srp.administracion.dto.response.UsuarioActualResponse;
import pe.unir.tfm.srp.administracion.dto.response.UsuarioInfoResponse;
import pe.unir.tfm.srp.administracion.dto.response.UsuarioResponse;
import pe.unir.tfm.srp.administracion.model.Usuario;

@Mapper(uses = {RolConversor.class})
public interface UsuarioConversor {

    @Mapping(target = "nombreCompleto",
             expression = "java(usuario.getNombres() + \" \" + usuario.getApellidoPaterno() + \" \" + usuario.getApellidoMaterno())")
    @Mapping(target = "rol", source = "rol")
    UsuarioInfoResponse aUsuarioInfo(Usuario usuario);

    @Mapping(target = "rol", source = "usuario.rol")
    UsuarioActualResponse aUsuarioActual(Usuario usuario, List<ModuloResponse> modulos);

    @Mapping(target = "rol", source = "rol")
    UsuarioResponse aUsuarioResponse(Usuario usuario);

    List<UsuarioResponse> aUsuarioResponseList(List<Usuario> usuarios);
}
