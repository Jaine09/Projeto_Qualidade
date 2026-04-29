package com.livraria.api.controller;

import com.livraria.entity.Livro;
import com.livraria.api.service.LivroService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livros")
public class LivroRestController {

    @Autowired
    private LivroService livroService;

    // =========================
    // CREATE
    // =========================
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Livro livro) {
        try {
            Livro salvo = livroService.salvar(livro);
            return ResponseEntity.status(201).body(salvo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =========================
    // READ ALL POR USUÁRIO
    // =========================
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> listarPorUsuario(@PathVariable String usuarioId) {

        if (usuarioId == null || usuarioId.isBlank()) {
            return ResponseEntity.badRequest().body("Usuário inválido");
        }

        List<Livro> livros = livroService.listarPorUsuario(usuarioId);
        return ResponseEntity.ok(livros);
    }

    // =========================
    // READ BY ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable String id) {

        if (id == null || id.isBlank()) {
            return ResponseEntity.badRequest().body("ID inválido");
        }

        return livroService.buscarPorId(id)
                .map(livro -> ResponseEntity.ok((Object) livro))
                .orElse(ResponseEntity.status(404).body("Livro não encontrado"));
    }

    // =========================
    // UPDATE
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable String id,
                                       @RequestBody Livro livro) {

        if (id == null || id.isBlank()) {
            return ResponseEntity.badRequest().body("ID inválido");
        }

        try {
            return livroService.atualizar(id, livro)
                    .map(l -> ResponseEntity.ok((Object) l))
                    .orElse(ResponseEntity.status(404).body("Livro não encontrado"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =========================
    // DELETE
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable String id) {

        if (id == null || id.isBlank()) {
            return ResponseEntity.badRequest().body("ID inválido");
        }

        if (livroService.deletar(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(404).body("Livro não encontrado");
    }
}