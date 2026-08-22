package pe.unir.tfm.srp.administracion.repository;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import pe.unir.tfm.srp.administracion.model.Rol;

@Mapper
public interface RolMapper {

    Rol findById(@Param("id") UUID id);

    Rol findByCode(@Param("codigo") String codigo);

    List<Rol> listAll();

    List<Rol> listActive();

    long countPaged(@Param("search") String search,
                     @Param("sistema") Boolean sistema,
                     @Param("estado") Integer estado);

    List<Rol> listPage(@Param("search") String search,
                         @Param("sistema") Boolean sistema,
                         @Param("estado") Integer estado,
                         @Param("pageSize") int pageSize,
                         @Param("offset") long offset,
                         @Param("sortClause") String sortClause);

    void insert(Rol rol);

    void update(Rol rol);

    long countUsosComoLanding(@Param("moduloId") UUID moduloId);

    void softDelete(@Param("id") UUID id,
                     @Param("usuarioEliminacion") UUID usuarioEliminacion,
                     @Param("motivoEliminacion") String motivoEliminacion);
}