package pe.unir.tfm.srp.administracion.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import pe.unir.tfm.srp.administracion.dto.request.CatalogoActualizarRequest;
import pe.unir.tfm.srp.administracion.dto.request.CatalogoCrearRequest;
import pe.unir.tfm.srp.administracion.dto.request.EliminacionRequest;
import pe.unir.tfm.srp.administracion.dto.response.CatalogoResponse;
import pe.unir.tfm.srp.administracion.service.CatalogoService;

@RestController
@RequestMapping("/api/catalogo")
@RequiredArgsConstructor
public class CatalogoController {

    private final CatalogoService catalogoService;

    @GetMapping
    public ResponseEntity<List<CatalogoResponse>> list() {
        return ResponseEntity.ok(catalogoService.list());
    }

    @GetMapping("/grupo/{grupo}")
    public ResponseEntity<List<CatalogoResponse>> listByGroup(@PathVariable String grupo) {
        return ResponseEntity.ok(catalogoService.listByGroup(grupo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CatalogoResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(catalogoService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CatalogoResponse> create(@Valid @RequestBody CatalogoCrearRequest request) {
        return ResponseEntity.ok(catalogoService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CatalogoResponse> update(@PathVariable UUID id,
                                                    @Valid @RequestBody CatalogoActualizarRequest request) {
        return ResponseEntity.ok(catalogoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @Valid @RequestBody EliminacionRequest request) {
        catalogoService.delete(id, request);
        return ResponseEntity.noContent().build();
    }
}