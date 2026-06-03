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
import pe.unir.tfm.srp.administracion.dto.request.AsignarModulosRolRequest;
import pe.unir.tfm.srp.administracion.dto.request.EliminacionRequest;
import pe.unir.tfm.srp.administracion.dto.request.RolActualizarRequest;
import pe.unir.tfm.srp.administracion.dto.request.RolCrearRequest;
import pe.unir.tfm.srp.administracion.dto.response.ModuloResponse;
import pe.unir.tfm.srp.administracion.dto.response.RolResponse;
import pe.unir.tfm.srp.administracion.service.RolService;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolController {

    private final RolService rolService;

    @GetMapping
    public ResponseEntity<List<RolResponse>> listar() {
        return ResponseEntity.ok(rolService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(rolService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RolResponse> crear(@Valid @RequestBody RolCrearRequest request) {
        return ResponseEntity.ok(rolService.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RolResponse> actualizar(@PathVariable UUID id,
                                                  @Valid @RequestBody RolActualizarRequest request) {
        return ResponseEntity.ok(rolService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id, @Valid @RequestBody EliminacionRequest request) {
        rolService.eliminar(id, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/modulos")
    public ResponseEntity<List<ModuloResponse>> listarModulos(@PathVariable UUID id) {
        return ResponseEntity.ok(rolService.listarModulosDelRol(id));
    }

    @PutMapping("/{id}/modulos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ModuloResponse>> reemplazarModulos(@PathVariable UUID id,
                                                                  @Valid @RequestBody AsignarModulosRolRequest request) {
        return ResponseEntity.ok(rolService.reemplazarModulosDelRol(id, request));
    }
}
