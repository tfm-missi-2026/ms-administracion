package pe.unir.tfm.srp.administracion.repository;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import pe.unir.tfm.srp.administracion.model.Catalogo;

@Mapper
public interface CatalogoMapper {

    Catalogo buscarPorId(@Param("id") UUID id);

    List<Catalogo> listarPorGrupo(@Param("grupo") String grupo);

    List<Catalogo> listarActivos();

    int contarPorGrupoIdOpcion(@Param("grupo") String grupo, @Param("idOpcion") Short idOpcion);

    void insertar(Catalogo catalogo);

    void actualizar(Catalogo catalogo);

    void eliminarLogico(@Param("id") UUID id,
                        @Param("usuarioEliminacion") UUID usuarioEliminacion,
                        @Param("motivoEliminacion") String motivoEliminacion);
}
