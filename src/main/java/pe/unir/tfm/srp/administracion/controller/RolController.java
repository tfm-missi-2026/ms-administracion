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
import pe.unir.tfm.srp.administracion.dto.request.RolQueryParams;
import pe.unir.tfm.srp.administracion.dto.response.ModuloResponse;
import pe.unir.tfm.srp.administracion.dto.response.PageData;
import pe.unir.tfm.srp.administracion.dto.response.RolResponse;
import pe.unir.tfm.srp.administracion.service.RolService;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolController {

    private final RolService rolService;

    @GetMapping
    public ResponseEntity<PageData<RolResponse>> list(@Valid RolQueryParams params) {
        return ResponseEntity.ok(rolService.listPage(params));
    }

    @GetMapping("/todos")
    public ResponseEntity<List<RolResponse>> listAll() {
        return ResponseEntity.ok(rolService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(rolService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RolResponse> create(@Valid @RequestBody RolCrearRequest request) {
        return ResponseEntity.ok(rolService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RolResponse> update(@PathVariable UUID id,
                                              @Valid @RequestBody RolActualizarRequest request) {
        return ResponseEntity.ok(rolService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @Valid @RequestBody EliminacionRequest request) {
        rolService.delete(id, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/modulos")
    public ResponseEntity<List<ModuloResponse>> listModules(@PathVariable UUID id) {
        return ResponseEntity.ok(rolService.listModulesForRole(id));
    }

    @PutMapping("/{id}/modulos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ModuloResponse>> replaceModules(@PathVariable UUID id,
                                                                @Valid @RequestBody AsignarModulosRolRequest request) {
        return ResponseEntity.ok(rolService.replaceModules(id, request));
    }
}