package pe.unir.tfm.srp.administracion.repository;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import pe.unir.tfm.srp.administracion.model.Rol;

@Mapper
public interface RolMapper {

    Rol buscarPorId(@Param("id") UUID id);

    Rol buscarPorCodigo(@Param("codigo") String codigo);

    List<Rol> listarActivos();

    void insertar(Rol rol);

    void actualizar(Rol rol);

    void eliminarLogico(@Param("id") UUID id,
                        @Param("usuarioEliminacion") UUID usuarioEliminacion,
                        @Param("motivoEliminacion") String motivoEliminacion);
}
