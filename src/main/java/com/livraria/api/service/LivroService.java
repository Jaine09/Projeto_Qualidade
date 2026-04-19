package com.livraria.api.service;

import com.livraria.entity.Livro;
import com.livraria.api.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LivroService {

    @Autowired
    private LivroRepository livroRepository;

    // CREATE
    public Livro salvar(Livro livro) {
        validar(livro);
        return livroRepository.save(livro);
    }

    // READ
    public List<Livro> listarPorUsuario(String usuarioId) {
        return livroRepository.findByUsuarioId(usuarioId);
    }

    public Optional<Livro> buscarPorId(String id) {
        return livroRepository.findById(id);
    }

    // UPDATE
    public Optional<Livro> atualizar(String id, Livro livroAtualizado) {
        return livroRepository.findById(id).map(livro -> {
            livro.setTitulo(livroAtualizado.getTitulo());
            livro.setAutor(livroAtualizado.getAutor());
            livro.setDescricao(livroAtualizado.getDescricao());
            return livroRepository.save(livro);
        });
    }

    // DELETE
    public boolean deletar(String id) {
        if (livroRepository.existsById(id)) {
            livroRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void validar(Livro livro) {
        if (livro.getTitulo() == null || livro.getTitulo().isBlank()) {
            throw new IllegalArgumentException("Título obrigatório");
        }
    }
}