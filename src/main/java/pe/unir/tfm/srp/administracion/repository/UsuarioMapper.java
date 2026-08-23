package pe.unir.tfm.srp.administracion.repository;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import pe.unir.tfm.srp.administracion.model.Usuario;

@Mapper
public interface UsuarioMapper {

    Usuario findById(@Param("id") UUID id);

    Usuario findByEmail(@Param("email") String email);

    List<Usuario> listActive();

    long countPaged(@Param("search") String search,
                    @Param("rolCodigo") String rolCodigo,
                    @Param("estado") Integer estado);

    List<Usuario> listPage(@Param("search") String search,
                           @Param("rolCodigo") String rolCodigo,
                           @Param("estado") Integer estado,
                           @Param("pageSize") int pageSize,
                           @Param("offset") long offset,
                           @Param("sortClause") String sortClause);

    int countByEmail(@Param("email") String email);

    void insert(Usuario usuario);

    void update(Usuario usuario);

    void softDelete(@Param("id") UUID id,
                     @Param("usuarioEliminacion") UUID usuarioEliminacion,
                     @Param("motivoEliminacion") String motivoEliminacion);

    int updatePassword(@Param("id") UUID id,
                        @Param("contraseniaHash") String contraseniaHash,
                        @Param("usuarioModificacion") UUID usuarioModificacion);
}