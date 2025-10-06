package com.devstoblu.banking_system.controllers;

import com.devstoblu.banking_system.models.Usuario;
import com.devstoblu.banking_system.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> buscarTodos(){
        return ResponseEntity.ok(usuarioService.buscarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Usuario> criarUsuario(@Valid @RequestBody Usuario usuario){
        return ResponseEntity.status(201).body(usuarioService.criarUsuario(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario dadosAtualizados){
        return ResponseEntity.ok(usuarioService.atualizarUsuario(id, dadosAtualizados));
    }

    @PutMapping("/{id}/bloquear")
    public ResponseEntity<Usuario> bloquearUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.bloquearUsuario(id));
    }

    @PutMapping("/{id}/reativar")
    public ResponseEntity<Usuario> reativarUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.reativarUsuario(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativarUsuario(@PathVariable Long id){
        usuarioService.inativarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
