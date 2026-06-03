package pe.unir.tfm.srp.administracion.repository;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import pe.unir.tfm.srp.administracion.model.Modulo;

@Mapper
public interface ModuloMapper {

    Modulo buscarPorId(@Param("id") UUID id);

    Modulo buscarPorCodigo(@Param("codigo") String codigo);

    List<Modulo> listarActivos();

    List<Modulo> listarPorRol(@Param("rolId") UUID rolId);

    void insertar(Modulo modulo);

    void actualizar(Modulo modulo);

    void eliminarLogico(@Param("id") UUID id,
                        @Param("usuarioEliminacion") UUID usuarioEliminacion,
                        @Param("motivoEliminacion") String motivoEliminacion);
}
