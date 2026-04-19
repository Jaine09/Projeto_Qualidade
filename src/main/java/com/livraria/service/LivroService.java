package com.livraria.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.livraria.entity.Livro;
import com.livraria.repository.LivroRepository;

import java.util.List;

@Service
public class LivroService {

    @Autowired
    private LivroRepository livroRepository;

    public Livro salvar(Livro livro) {
        return livroRepository.save(livro);
    }

    public List<Livro> listarPorUsuario(String usuarioId) {
        return livroRepository.findByUsuarioId(usuarioId);
    }

    public void deletar(String id) {
        livroRepository.deleteById(id);
    }

    public Livro buscarPorId(String id) {
        return livroRepository.findById(id).orElse(null);
    }
}