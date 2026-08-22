package pe.unir.tfm.srp.administracion.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.unir.tfm.srp.administracion.config.CurrentUserResolver;
import pe.unir.tfm.srp.administracion.dto.conversor.CatalogoConversor;
import pe.unir.tfm.srp.administracion.dto.request.CatalogoActualizarRequest;
import pe.unir.tfm.srp.administracion.dto.request.CatalogoCrearRequest;
import pe.unir.tfm.srp.administracion.dto.request.EliminacionRequest;
import pe.unir.tfm.srp.administracion.dto.response.CatalogoResponse;
import pe.unir.tfm.srp.administracion.exception.ConflictoNegocioException;
import pe.unir.tfm.srp.administracion.exception.RecursoNoEncontradoException;
import pe.unir.tfm.srp.administracion.model.Catalogo;
import pe.unir.tfm.srp.administracion.repository.CatalogoMapper;

@Service
@RequiredArgsConstructor
public class CatalogoService {

    private final CatalogoMapper catalogoMapper;
    private final CatalogoConversor catalogoConversor;
    private final CurrentUserResolver currentUserResolver;

    public List<CatalogoResponse> list() {
        return catalogoConversor.aCatalogoResponseList(catalogoMapper.listActive());
    }

    public List<CatalogoResponse> listByGroup(String grupo) {
        return catalogoConversor.aCatalogoResponseList(catalogoMapper.listByGroup(grupo));
    }

    public CatalogoResponse findById(UUID id) {
        Catalogo catalogo = catalogoMapper.findById(id);
        if (catalogo == null) {
            throw new RecursoNoEncontradoException("Catalogo " + id + " no encontrado");
        }
        return catalogoConversor.aCatalogoResponse(catalogo);
    }

    @Transactional
    public CatalogoResponse create(CatalogoCrearRequest request) {
        if (catalogoMapper.countByGroupAndOptionId(request.grupo(), request.idOpcion()) > 0) {
            throw new ConflictoNegocioException(
                    "Ya existe un catalogo con grupo " + request.grupo() + " e idOpcion " + request.idOpcion());
        }
        Catalogo nuevo = Catalogo.builder()
                .grupo(request.grupo())
                .idOpcion(request.idOpcion())
                .opcion(request.opcion())
                .build();
        catalogoMapper.insert(nuevo);
        return catalogoConversor.aCatalogoResponse(nuevo);
    }

    @Transactional
    public CatalogoResponse update(UUID id, CatalogoActualizarRequest request) {
        Catalogo existente = catalogoMapper.findById(id);
        if (existente == null) {
            throw new RecursoNoEncontradoException("Catalogo " + id + " no encontrado");
        }
        existente.setGrupo(request.grupo());
        existente.setIdOpcion(request.idOpcion());
        existente.setOpcion(request.opcion());
        catalogoMapper.update(existente);
        return catalogoConversor.aCatalogoResponse(existente);
    }

    @Transactional
    public void delete(UUID id, EliminacionRequest request) {
        Catalogo existente = catalogoMapper.findById(id);
        if (existente == null) {
            throw new RecursoNoEncontradoException("Catalogo " + id + " no encontrado");
        }
        catalogoMapper.softDelete(id, currentUserResolver.obtenerUsuarioActualId(), request.motivoEliminacion());
    }
}