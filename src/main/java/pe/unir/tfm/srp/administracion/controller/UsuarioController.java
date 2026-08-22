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
import pe.unir.tfm.srp.administracion.config.CurrentUserResolver;
import pe.unir.tfm.srp.administracion.dto.request.EliminacionRequest;
import pe.unir.tfm.srp.administracion.dto.request.ResetPasswordRequest;
import pe.unir.tfm.srp.administracion.dto.request.UsuarioActualizarRequest;
import pe.unir.tfm.srp.administracion.dto.request.UsuarioCrearRequest;
import pe.unir.tfm.srp.administracion.dto.response.UsuarioActualResponse;
import pe.unir.tfm.srp.administracion.dto.response.UsuarioResponse;
import pe.unir.tfm.srp.administracion.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final CurrentUserResolver currentUserResolver;

    @GetMapping("/me")
    public ResponseEntity<UsuarioActualResponse> getCurrent() {
        return ResponseEntity.ok(usuarioService.getCurrent(currentUserResolver.obtenerUsuarioActualId()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioResponse>> list() {
        return ResponseEntity.ok(usuarioService.list());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> create(@Valid @RequestBody UsuarioCrearRequest request) {
        return ResponseEntity.ok(usuarioService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> update(@PathVariable UUID id,
                                                 @Valid @RequestBody UsuarioActualizarRequest request) {
        return ResponseEntity.ok(usuarioService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @Valid @RequestBody EliminacionRequest request) {
        usuarioService.delete(id, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> resetPassword(@PathVariable UUID id,
                                              @Valid @RequestBody ResetPasswordRequest request) {
        usuarioService.resetPassword(id, request.contrasenia());
        return ResponseEntity.noContent().build();
    }
}