package com.livraria.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.livraria.entity.Usuario;
import com.livraria.repository.UsuarioRepository;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario salvar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> login(String email, String senha) {

        Optional<Usuario> user = usuarioRepository.findByEmail(email);

        if (user.isPresent() && user.get().getSenha().equals(senha)) {
            return user;
        }

        return Optional.empty();
    }
}