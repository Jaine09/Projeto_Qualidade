package com.livraria.api.controller;

import com.livraria.api.repository.UsuarioRepository;
import com.livraria.entity.Usuario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import org.springframework.http.*;

import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class UsuarioRestControllerTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void limparBanco() {
        usuarioRepository.deleteAll();
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void deveCriarUsuario() {
        Usuario usuario = new Usuario("Giulia", "giulia@email.com", "123");

        ResponseEntity<Usuario> response = restTemplate.postForEntity(
                url("/api/usuarios"),
                usuario,
                Usuario.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("Giulia", response.getBody().getNome());
        assertNull(response.getBody().getSenha());
    }

    @Test
    void naoDeveCriarUsuarioSemNome() {
        Usuario usuario = new Usuario("", "email@email.com", "123");

        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/usuarios"),
                usuario,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void naoDeveCriarUsuarioComEmailDuplicado() {
        Usuario usuario = new Usuario("Giulia", "giulia@email.com", "123");

        restTemplate.postForEntity(url("/api/usuarios"), usuario, Usuario.class);

        Usuario duplicado = new Usuario("Outra", "giulia@email.com", "456");

        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/usuarios"),
                duplicado,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void deveFazerLogin() {
        Usuario usuario = new Usuario("Giulia", "giulia@email.com", "123");

        restTemplate.postForEntity(url("/api/usuarios"), usuario, Usuario.class);

        Usuario login = new Usuario(null, "giulia@email.com", "123");

        ResponseEntity<Usuario> response = restTemplate.postForEntity(
                url("/api/usuarios/login"),
                login,
                Usuario.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("giulia@email.com", response.getBody().getEmail());
        assertNull(response.getBody().getSenha());
    }

    @Test
    void naoDeveFazerLoginComSenhaErrada() {
        Usuario usuario = new Usuario("Giulia", "giulia@email.com", "123");

        restTemplate.postForEntity(url("/api/usuarios"), usuario, Usuario.class);

        Usuario loginErrado = new Usuario(null, "giulia@email.com", "errada");

        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/usuarios/login"),
                loginErrado,
                String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void deveListarUsuarios() {
        restTemplate.postForEntity(
                url("/api/usuarios"),
                new Usuario("Nome", "email@email.com", "123"),
                Usuario.class
        );

        ResponseEntity<Usuario[]> response = restTemplate.getForEntity(
                url("/api/usuarios"),
                Usuario[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
        assertNull(response.getBody()[0].getSenha());
    }

    @Test
    void deveBuscarUsuarioPorId() {
        ResponseEntity<Usuario> criado = restTemplate.postForEntity(
                url("/api/usuarios"),
                new Usuario("Nome", "email@email.com", "123"),
                Usuario.class
        );

        assertNotNull(criado.getBody());

        ResponseEntity<Usuario> response = restTemplate.getForEntity(
                url("/api/usuarios/" + criado.getBody().getId()),
                Usuario.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("email@email.com", response.getBody().getEmail());
        assertNull(response.getBody().getSenha());
    }

    @Test
    void deveRetornar404AoBuscarUsuarioInexistente() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                url("/api/usuarios/id-invalido"),
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deveAtualizarUsuario() {
        ResponseEntity<Usuario> criado = restTemplate.postForEntity(
                url("/api/usuarios"),
                new Usuario("Nome", "email@email.com", "123"),
                Usuario.class
        );

        assertNotNull(criado.getBody());

        Usuario atualizado = new Usuario("Novo Nome", "novo@email.com", "456");

        HttpEntity<Usuario> request = new HttpEntity<>(atualizado);

        ResponseEntity<Usuario> response = restTemplate.exchange(
                url("/api/usuarios/" + criado.getBody().getId()),
                HttpMethod.PUT,
                request,
                Usuario.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Novo Nome", response.getBody().getNome());
        assertEquals("novo@email.com", response.getBody().getEmail());
        assertNull(response.getBody().getSenha());
    }

    @Test
    void deveRetornar404AoAtualizarUsuarioInexistente() {
        Usuario atualizado = new Usuario("Novo Nome", "novo@email.com", "456");

        HttpEntity<Usuario> request = new HttpEntity<>(atualizado);

        ResponseEntity<String> response = restTemplate.exchange(
                url("/api/usuarios/id-invalido"),
                HttpMethod.PUT,
                request,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deveDeletarUsuario() {
        ResponseEntity<Usuario> criado = restTemplate.postForEntity(
                url("/api/usuarios"),
                new Usuario("Nome", "email@email.com", "123"),
                Usuario.class
        );

        assertNotNull(criado.getBody());

        ResponseEntity<Void> response = restTemplate.exchange(
                url("/api/usuarios/" + criado.getBody().getId()),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertTrue(usuarioRepository.findById(criado.getBody().getId()).isEmpty());
    }

    @Test
    void naoDeveDeletarUsuarioInexistente() {
        ResponseEntity<String> response = restTemplate.exchange(
                url("/api/usuarios/id-invalido"),
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}