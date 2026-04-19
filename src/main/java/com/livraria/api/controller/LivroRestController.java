package com.livraria.api.controller;

import com.livraria.entity.Livro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.livraria.api.service.LivroService;
import java.util.List;

@RestController
@RequestMapping("/api/livros")
public class LivroRestController {

    @Autowired
    private LivroService livroService;

    // CREATE
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Livro livro) {
        Livro salvo = livroService.salvar(livro);
        return ResponseEntity.status(201).body(salvo);
    }

    // READ ALL POR USUÁRIO
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Livro>> listarPorUsuario(@PathVariable String usuarioId) {
        return ResponseEntity.ok(livroService.listarPorUsuario(usuarioId));
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable String id) {
        return livroService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable String id,
                                       @RequestBody Livro livro) {

        return livroService.atualizar(id, livro)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable String id) {

        if (livroService.deletar(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}