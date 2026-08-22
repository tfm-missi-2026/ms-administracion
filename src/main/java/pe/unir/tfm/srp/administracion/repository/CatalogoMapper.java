package pe.unir.tfm.srp.administracion.repository;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import pe.unir.tfm.srp.administracion.model.Catalogo;

@Mapper
public interface CatalogoMapper {

    Catalogo findById(@Param("id") UUID id);

    List<Catalogo> listActive();

    List<Catalogo> listByGroup(@Param("grupo") String grupo);

    int countByGroupAndOptionId(@Param("grupo") String grupo,
                                  @Param("idOpcion") Short idOpcion);

    void insert(Catalogo catalogo);

    void update(Catalogo catalogo);

    void softDelete(@Param("id") UUID id,
                     @Param("usuarioEliminacion") UUID usuarioEliminacion,
                     @Param("motivoEliminacion") String motivoEliminacion);
}