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
import pe.unir.tfm.srp.administracion.dto.request.EliminacionRequest;
import pe.unir.tfm.srp.administracion.dto.request.ModuloActualizarRequest;
import pe.unir.tfm.srp.administracion.dto.request.ModuloCrearRequest;
import pe.unir.tfm.srp.administracion.dto.response.ModuloResponse;
import pe.unir.tfm.srp.administracion.service.ModuloService;

@RestController
@RequestMapping("/api/modulos")
@RequiredArgsConstructor
public class ModuloController {

    private final ModuloService moduloService;

    @GetMapping
    public ResponseEntity<List<ModuloResponse>> listar() {
        return ResponseEntity.ok(moduloService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModuloResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(moduloService.buscarPorId(id));
    }

    @GetMapping("/por-rol/{rolId}")
    public ResponseEntity<List<ModuloResponse>> listarPorRol(@PathVariable UUID rolId) {
        return ResponseEntity.ok(moduloService.listarPorRol(rolId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ModuloResponse> crear(@Valid @RequestBody ModuloCrearRequest request) {
        return ResponseEntity.ok(moduloService.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ModuloResponse> actualizar(@PathVariable UUID id,
                                                     @Valid @RequestBody ModuloActualizarRequest request) {
        return ResponseEntity.ok(moduloService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id, @Valid @RequestBody EliminacionRequest request) {
        moduloService.eliminar(id, request);
        return ResponseEntity.noContent().build();
    }
}
