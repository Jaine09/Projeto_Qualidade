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

    public Livro salvar(Livro livro) {
        validar(livro);
        return livroRepository.save(livro);
    }

    public List<Livro> listarPorUsuario(String usuarioId) {
        return livroRepository.findByUsuarioId(usuarioId);
    }

    public Optional<Livro> buscarPorId(String id) {
        return livroRepository.findById(id);
    }

    public Optional<Livro> atualizar(String id, Livro dados) {
        validar(dados);

        return livroRepository.findById(id).map(livro -> {
            atualizarCampos(livro, dados);
            return livroRepository.save(livro);
        });
    }

    public boolean deletar(String id) {
        return livroRepository.findById(id)
                .map(livro -> {
                    livroRepository.delete(livro);
                    return true;
                })
                .orElse(false);
    }

    private void atualizarCampos(Livro livro, Livro dados) {
        livro.setTitulo(dados.getTitulo());
        livro.setAutor(dados.getAutor());
        livro.setGenero(dados.getGenero());
        livro.setDescricao(dados.getDescricao());
        livro.setImagem(dados.getImagem());
    }

    private void validar(Livro livro) {
        if (livro.getTitulo() == null || livro.getTitulo().isBlank()) {
            throw new IllegalArgumentException("Título obrigatório");
        }

        if (livro.getUsuarioId() == null || livro.getUsuarioId().isBlank()) {
            throw new IllegalArgumentException("Usuário obrigatório");
        }
    }
}