package com.livraria.api.service;

import com.livraria.api.repository.UsuarioRepository;
import com.livraria.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public Usuario salvar(Usuario usuario) {
        validarCadastro(usuario);

        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        usuario.setSenha(encoder.encode(usuario.getSenha()));

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(String id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> atualizar(String id, Usuario usuarioAtualizado) {
        return usuarioRepository.findById(id).map(usuario -> {

            validarAtualizacao(usuarioAtualizado);

            if (!usuario.getEmail().equals(usuarioAtualizado.getEmail())
                    && usuarioRepository.findByEmail(usuarioAtualizado.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email já cadastrado");
            }

            usuario.setNome(usuarioAtualizado.getNome());
            usuario.setEmail(usuarioAtualizado.getEmail());
            usuario.setCep(usuarioAtualizado.getCep());
            usuario.setEndereco(usuarioAtualizado.getEndereco());
            usuario.setCidade(usuarioAtualizado.getCidade());
            usuario.setEstado(usuarioAtualizado.getEstado());

            if (usuarioAtualizado.getSenha() != null
                    && !usuarioAtualizado.getSenha().isBlank()) {
                usuario.setSenha(encoder.encode(usuarioAtualizado.getSenha()));
            }

            return usuarioRepository.save(usuario);
        });
    }

    public boolean deletar(String id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }

        return false;
    }

    public Optional<Usuario> login(String email, String senha) {
        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            return Optional.empty();
        }

        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);

        if (usuario.isPresent() && encoder.matches(senha, usuario.get().getSenha())) {
            return usuario;
        }

        return Optional.empty();
    }

    private void validarCadastro(Usuario usuario) {
        validarDadosBasicos(usuario);

        if (usuario.getSenha() == null || usuario.getSenha().isBlank()) {
            throw new IllegalArgumentException("Senha obrigatória");
        }
    }

    private void validarAtualizacao(Usuario usuario) {
        validarDadosBasicos(usuario);
    }

    private void validarDadosBasicos(Usuario usuario) {
        if (usuario.getNome() == null || usuario.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome obrigatório");
        }

        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email obrigatório");
        }

        if (usuario.getCep() == null || usuario.getCep().isBlank()) {
            throw new IllegalArgumentException("CEP obrigatório");
        }

        if (usuario.getEndereco() == null || usuario.getEndereco().isBlank()) {
            throw new IllegalArgumentException("Endereço obrigatório");
        }

        if (usuario.getCidade() == null || usuario.getCidade().isBlank()) {
            throw new IllegalArgumentException("Cidade obrigatória");
        }

        if (usuario.getEstado() == null || usuario.getEstado().isBlank()) {
            throw new IllegalArgumentException("Estado obrigatório");
        }
    }
}