package pe.unir.tfm.srp.administracion.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import pe.unir.tfm.srp.administracion.dto.request.ModuloActualizarRequest;
import pe.unir.tfm.srp.administracion.dto.request.ModuloCrearRequest;
import pe.unir.tfm.srp.administracion.dto.request.ModuloEstadoRequest;
import pe.unir.tfm.srp.administracion.dto.request.ModuloQueryParams;
import pe.unir.tfm.srp.administracion.dto.response.ModuloResponse;
import pe.unir.tfm.srp.administracion.dto.response.PageData;
import pe.unir.tfm.srp.administracion.service.ModuloService;

@RestController
@RequestMapping("/api/modulos")
@RequiredArgsConstructor
public class ModuloController {

    private final ModuloService moduloService;

    @GetMapping
    public ResponseEntity<PageData<ModuloResponse>> list(@Valid ModuloQueryParams params) {
        return ResponseEntity.ok(moduloService.listPage(params));
    }

    @GetMapping("/todos")
    public ResponseEntity<List<ModuloResponse>> listAll() {
        return ResponseEntity.ok(moduloService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModuloResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(moduloService.findById(id));
    }

    @GetMapping("/por-rol/{rolId}")
    public ResponseEntity<List<ModuloResponse>> listByRole(@PathVariable UUID rolId) {
        return ResponseEntity.ok(moduloService.listByRole(rolId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ModuloResponse> create(@Valid @RequestBody ModuloCrearRequest request) {
        return ResponseEntity.ok(moduloService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ModuloResponse> update(@PathVariable UUID id,
                                                @Valid @RequestBody ModuloActualizarRequest request) {
        return ResponseEntity.ok(moduloService.update(id, request));
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ModuloResponse> changeState(@PathVariable UUID id,
                                                     @Valid @RequestBody ModuloEstadoRequest request) {
        return ResponseEntity.ok(moduloService.changeState(id, request));
    }
}