package com.livraria.api.service;

import com.livraria.entity.Usuario;
import com.livraria.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // CREATE
    public Usuario salvar(Usuario usuario) {
        validar(usuario);
        return usuarioRepository.save(usuario);
    }

    // READ ALL
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    // READ BY ID
    public Optional<Usuario> buscarPorId(String id) {
        return usuarioRepository.findById(id);
    }

    // UPDATE
    public Optional<Usuario> atualizar(String id, Usuario usuarioAtualizado) {
        Optional<Usuario> existente = usuarioRepository.findById(id);

        if (existente.isPresent()) {
            Usuario usuario = existente.get();

            usuario.setNome(usuarioAtualizado.getNome());
            usuario.setEmail(usuarioAtualizado.getEmail());
            usuario.setSenha(usuarioAtualizado.getSenha());

            return Optional.of(usuarioRepository.save(usuario));
        }

        return Optional.empty();
    }

    // DELETE
    public boolean deletar(String id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // LOGIN
    public Optional<Usuario> login(String email, String senha) {
        Optional<Usuario> user = usuarioRepository.findByEmail(email);

        if (user.isPresent() && user.get().getSenha().equals(senha)) {
            return user;
        }

        return Optional.empty();
    }

    private void validar(Usuario usuario) {
        if (usuario.getNome() == null || usuario.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome obrigatório");
        }
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email obrigatório");
        }
        if (usuario.getSenha() == null || usuario.getSenha().isBlank()) {
            throw new IllegalArgumentException("Senha obrigatória");
        }
    }
}