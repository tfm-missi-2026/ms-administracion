package pe.unir.tfm.srp.administracion.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.unir.tfm.srp.administracion.dto.conversor.CatalogoConversor;
import pe.unir.tfm.srp.administracion.dto.request.CatalogoActualizarRequest;
import pe.unir.tfm.srp.administracion.dto.request.CatalogoCrearRequest;
import pe.unir.tfm.srp.administracion.dto.request.EliminacionRequest;
import pe.unir.tfm.srp.administracion.dto.response.CatalogoResponse;
import pe.unir.tfm.srp.administracion.config.CurrentUserResolver;
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

    public List<CatalogoResponse> listar() {
        return catalogoConversor.aCatalogoResponseList(catalogoMapper.listarActivos());
    }

    public List<CatalogoResponse> listarPorGrupo(String grupo) {
        return catalogoConversor.aCatalogoResponseList(catalogoMapper.listarPorGrupo(grupo));
    }

    public CatalogoResponse buscarPorId(UUID id) {
        Catalogo catalogo = catalogoMapper.buscarPorId(id);
        if (catalogo == null) {
            throw new RecursoNoEncontradoException("Item de catalogo " + id + " no encontrado");
        }
        return catalogoConversor.aCatalogoResponse(catalogo);
    }

    @Transactional
    public CatalogoResponse crear(CatalogoCrearRequest request) {
        if (catalogoMapper.contarPorGrupoIdOpcion(request.grupo(), request.idOpcion()) > 0) {
            throw new ConflictoNegocioException(
                    "Ya existe un item con grupo=" + request.grupo() + " e id_opcion=" + request.idOpcion());
        }
        Catalogo nuevo = Catalogo.builder()
                .id(UUID.randomUUID())
                .grupo(request.grupo())
                .idOpcion(request.idOpcion())
                .opcion(request.opcion())
                .build();
        catalogoMapper.insertar(nuevo);
        return catalogoConversor.aCatalogoResponse(nuevo);
    }

    @Transactional
    public CatalogoResponse actualizar(UUID id, CatalogoActualizarRequest request) {
        Catalogo existente = catalogoMapper.buscarPorId(id);
        if (existente == null) {
            throw new RecursoNoEncontradoException("Item de catalogo " + id + " no encontrado");
        }
        existente.setGrupo(request.grupo());
        existente.setIdOpcion(request.idOpcion());
        existente.setOpcion(request.opcion());
        catalogoMapper.actualizar(existente);
        return catalogoConversor.aCatalogoResponse(existente);
    }

    @Transactional
    public void eliminar(UUID id, EliminacionRequest request) {
        Catalogo existente = catalogoMapper.buscarPorId(id);
        if (existente == null) {
            throw new RecursoNoEncontradoException("Item de catalogo " + id + " no encontrado");
        }
        catalogoMapper.eliminarLogico(id, currentUserResolver.obtenerUsuarioActualId(), request.motivoEliminacion());
    }
}
