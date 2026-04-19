package com.livraria.api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.livraria.entity.Livro;

import java.util.List;

public interface LivroRepository extends MongoRepository<Livro, String> {

    List<Livro> findByUsuarioId(String usuarioId);
}