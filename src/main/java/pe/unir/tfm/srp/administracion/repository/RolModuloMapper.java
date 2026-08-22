package pe.unir.tfm.srp.administracion.repository;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import pe.unir.tfm.srp.administracion.model.RolModulo;

@Mapper
public interface RolModuloMapper {

    List<RolModulo> listByRole(@Param("rolId") UUID rolId);

    int count(@Param("rolId") UUID rolId, @Param("moduloId") UUID moduloId);

    void insert(RolModulo rolModulo);

    void deleteByRole(@Param("rolId") UUID rolId);
}