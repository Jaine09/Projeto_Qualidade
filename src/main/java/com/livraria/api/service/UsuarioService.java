package com.livraria.api.service;

import com.livraria.entity.Usuario;
import com.livraria.api.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // =========================
    // CREATE
    // =========================
    public Usuario salvar(Usuario usuario) {

        validar(usuario);

        // 🔴 Verifica email duplicado
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        // 🔐 Criptografar senha
        usuario.setSenha(encoder.encode(usuario.getSenha()));

        return usuarioRepository.save(usuario);
    }

    // =========================
    // READ ALL
    // =========================
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    // =========================
    // READ BY ID
    // =========================
    public Optional<Usuario> buscarPorId(String id) {
        return usuarioRepository.findById(id);
    }

    // =========================
    // UPDATE
    // =========================
    public Optional<Usuario> atualizar(String id, Usuario usuarioAtualizado) {

        return usuarioRepository.findById(id).map(usuario -> {

            // 🔴 validações obrigatórias
            if (usuarioAtualizado.getNome() == null || usuarioAtualizado.getNome().isBlank()) {
                throw new IllegalArgumentException("Nome obrigatório");
            }

            if (usuarioAtualizado.getEmail() == null || usuarioAtualizado.getEmail().isBlank()) {
                throw new IllegalArgumentException("Email obrigatório");
            }

            // 🔴 verifica email duplicado (se mudou)
            if (!usuario.getEmail().equals(usuarioAtualizado.getEmail())) {
                if (usuarioRepository.findByEmail(usuarioAtualizado.getEmail()).isPresent()) {
                    throw new IllegalArgumentException("Email já cadastrado");
                }
            }

            usuario.setNome(usuarioAtualizado.getNome());
            usuario.setEmail(usuarioAtualizado.getEmail());

            // 🔐 Atualiza senha apenas se informada
            if (usuarioAtualizado.getSenha() != null &&
                !usuarioAtualizado.getSenha().isBlank()) {

                usuario.setSenha(encoder.encode(usuarioAtualizado.getSenha()));
            }

            return usuarioRepository.save(usuario);
        });
    }

    // =========================
    // DELETE
    // =========================
    public boolean deletar(String id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // =========================
    // LOGIN
    // =========================
    public Optional<Usuario> login(String email, String senha) {

        Optional<Usuario> user = usuarioRepository.findByEmail(email);

        if (user.isPresent() && encoder.matches(senha, user.get().getSenha())) {
            return user;
        }

        return Optional.empty();
    }

    // =========================
    // VALIDAÇÕES
    // =========================
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