package pe.unir.tfm.srp.administracion.repository;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import pe.unir.tfm.srp.administracion.model.RolModulo;

@Mapper
public interface RolModuloMapper {

    List<RolModulo> listarPorRol(@Param("rolId") UUID rolId);

    int contar(@Param("rolId") UUID rolId, @Param("moduloId") UUID moduloId);

    void insertar(RolModulo rolModulo);

    void eliminarFisicoPorRol(@Param("rolId") UUID rolId);
}
