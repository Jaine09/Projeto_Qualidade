package com.livraria.api.controller;

import com.livraria.entity.Usuario;
import com.livraria.api.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioRestController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Usuario usuario) {
        try {
            Usuario salvo = usuarioService.salvar(usuario);
            salvo.setSenha(null);
            return ResponseEntity.status(201).body(salvo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        List<Usuario> usuarios = usuarioService.listar();
        usuarios.forEach(u -> u.setSenha(null));
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable String id) {
        return usuarioService.buscarPorId(id)
                .map(usuario -> {
                    usuario.setSenha(null);
                    return ResponseEntity.ok(usuario);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable String id,
                                       @RequestBody Usuario usuario) {
        try {
            return usuarioService.atualizar(id, usuario)
                    .map(usuarioAtualizado -> {
                        usuarioAtualizado.setSenha(null);
                        return ResponseEntity.ok(usuarioAtualizado);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable String id) {
        if (usuarioService.deletar(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario usuario) {
        Optional<Usuario> usuarioEncontrado = usuarioService.login(
                usuario.getEmail(),
                usuario.getSenha()
        );

        if (usuarioEncontrado.isPresent()) {
            Usuario usuarioLogado = usuarioEncontrado.get();
            usuarioLogado.setSenha(null);
            return ResponseEntity.ok(usuarioLogado);
        }

        return ResponseEntity.status(401).body("Email ou senha inválidos");
    }
}