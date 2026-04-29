package com.livraria.api.service;

import com.livraria.entity.Usuario;
import com.livraria.api.repository.UsuarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class UsuarioServiceIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void limparBanco() {
        usuarioRepository.deleteAll();
    }

    @Test
    void deveSalvarUsuarioComSucesso() {
        Usuario usuario = new Usuario("Giulia", "giulia@email.com", "123456");

        Usuario salvo = usuarioService.salvar(usuario);

        assertNotNull(salvo.getId());
        assertNotEquals("123456", salvo.getSenha());
    }

    @Test
    void naoDeveSalvarUsuarioSemNome() {
        Usuario usuario = new Usuario("", "email@email.com", "123");

        assertThrows(IllegalArgumentException.class, () -> usuarioService.salvar(usuario));
    }

    @Test
    void naoDeveSalvarUsuarioSemEmail() {
        Usuario usuario = new Usuario("Nome", "", "123");

        assertThrows(IllegalArgumentException.class, () -> usuarioService.salvar(usuario));
    }

    @Test
    void naoDeveSalvarUsuarioSemSenha() {
        Usuario usuario = new Usuario("Nome", "email@email.com", "");

        assertThrows(IllegalArgumentException.class, () -> usuarioService.salvar(usuario));
    }

    @Test
    void naoDeveSalvarUsuarioComEmailDuplicado() {
        usuarioService.salvar(new Usuario("Nome", "email@email.com", "123"));

        Usuario duplicado = new Usuario("Outro", "email@email.com", "456");

        assertThrows(IllegalArgumentException.class, () -> usuarioService.salvar(duplicado));
    }

    @Test
    void deveFazerLoginComSucesso() {
        usuarioService.salvar(new Usuario("Nome", "email@email.com", "123"));

        Optional<Usuario> usuario = usuarioService.login("email@email.com", "123");

        assertTrue(usuario.isPresent());
    }

    @Test
    void naoDeveFazerLoginComSenhaErrada() {
        usuarioService.salvar(new Usuario("Nome", "email@email.com", "123"));

        Optional<Usuario> usuario = usuarioService.login("email@email.com", "senhaErrada");

        assertTrue(usuario.isEmpty());
    }

    @Test
    void naoDeveFazerLoginComEmailInexistente() {
        Optional<Usuario> usuario = usuarioService.login("naoexiste@email.com", "123");

        assertTrue(usuario.isEmpty());
    }

    @Test
    void deveAtualizarUsuario() {
        Usuario salvo = usuarioService.salvar(
                new Usuario("Nome", "email@email.com", "123")
        );

        Usuario atualizado = new Usuario("Novo Nome", "novo@email.com", "456");

        Optional<Usuario> resultado = usuarioService.atualizar(salvo.getId(), atualizado);

        assertTrue(resultado.isPresent());
        assertEquals("Novo Nome", resultado.get().getNome());
        assertEquals("novo@email.com", resultado.get().getEmail());
    }

    @Test
    void naoDeveAtualizarUsuarioInexistente() {
        Usuario usuario = new Usuario("Nome", "email@email.com", "123");

        Optional<Usuario> resultado = usuarioService.atualizar("id-invalido", usuario);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveDeletarUsuario() {
        Usuario salvo = usuarioService.salvar(
                new Usuario("Nome", "email@email.com", "123")
        );

        boolean deletado = usuarioService.deletar(salvo.getId());

        assertTrue(deletado);
        assertTrue(usuarioRepository.findById(salvo.getId()).isEmpty());
    }

    @Test
    void naoDeveDeletarUsuarioInexistente() {
        boolean resultado = usuarioService.deletar("id-invalido");

        assertFalse(resultado);
    }
}