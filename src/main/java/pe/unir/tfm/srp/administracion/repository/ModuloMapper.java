package pe.unir.tfm.srp.administracion.repository;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import pe.unir.tfm.srp.administracion.model.Modulo;

@Mapper
public interface ModuloMapper {

    Modulo findById(@Param("id") UUID id);

    Modulo findByIdWithState(@Param("id") UUID id);

    Modulo findByCode(@Param("codigo") String codigo);

    Modulo findSectionByCode(@Param("codigo") String codigo);

    List<Modulo> listActive();

    List<Modulo> listAll();

    List<Modulo> listByRole(@Param("rolId") UUID rolId);

    long countPaged(@Param("search") String search,
                     @Param("seccion") String seccion,
                     @Param("estado") Integer estado);

    List<Modulo> listPage(@Param("search") String search,
                           @Param("seccion") String seccion,
                           @Param("estado") Integer estado,
                           @Param("pageSize") int pageSize,
                           @Param("offset") long offset,
                           @Param("sortClause") String sortClause);

    void insert(Modulo modulo);

    void update(Modulo modulo);

    void changeState(@Param("id") UUID id,
                      @Param("estado") Short estado,
                      @Param("usuarioModificacion") UUID usuarioModificacion,
                      @Param("motivoEliminacion") String motivoEliminacion);
}