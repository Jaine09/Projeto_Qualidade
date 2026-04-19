package com.livraria.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.livraria.entity.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByEmail(String email);
}