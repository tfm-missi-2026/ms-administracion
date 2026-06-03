package pe.unir.tfm.srp.administracion.repository;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import pe.unir.tfm.srp.administracion.model.Usuario;

@Mapper
public interface UsuarioMapper {

    Usuario buscarPorId(@Param("id") UUID id);

    Usuario buscarPorEmail(@Param("email") String email);

    List<Usuario> listarActivos();

    int contarPorEmail(@Param("email") String email);

    void insertar(Usuario usuario);

    void actualizar(Usuario usuario);

    void eliminarLogico(@Param("id") UUID id,
                        @Param("usuarioEliminacion") UUID usuarioEliminacion,
                        @Param("motivoEliminacion") String motivoEliminacion);
}
