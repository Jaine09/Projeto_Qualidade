package com.livraria.api.controller;

import com.livraria.api.repository.LivroRepository;
import com.livraria.entity.Livro;

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
public class LivroRestControllerTest {

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
    private LivroRepository livroRepository;

    @BeforeEach
    void limparBanco() {
        livroRepository.deleteAll();
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void deveCriarLivro() {
        Livro livro = new Livro(
                "Livro Teste",
                "Autor",
                "Gênero",
                "Descrição",
                "img",
                "user1"
        );

        ResponseEntity<Livro> response = restTemplate.postForEntity(
                url("/api/livros"),
                livro,
                Livro.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("Livro Teste", response.getBody().getTitulo());
    }

    @Test
    void deveRetornar400AoCriarLivroSemTitulo() {
        Livro livro = new Livro(
                "",
                "Autor",
                "Gênero",
                "Descrição",
                "img",
                "user1"
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/livros"),
                livro,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void deveRetornar400AoCriarLivroSemUsuario() {
        Livro livro = new Livro(
                "Título",
                "Autor",
                "Gênero",
                "Descrição",
                "img",
                ""
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/livros"),
                livro,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void deveListarLivrosPorUsuario() {
        livroRepository.save(new Livro("Livro 1", "Autor", "Gênero", "Descrição", "img", "user1"));
        livroRepository.save(new Livro("Livro 2", "Autor", "Gênero", "Descrição", "img", "user1"));
        livroRepository.save(new Livro("Livro 3", "Autor", "Gênero", "Descrição", "img", "user2"));

        ResponseEntity<Livro[]> response = restTemplate.getForEntity(
                url("/api/livros/usuario/user1"),
                Livro[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().length);
    }

    @Test
    void deveBuscarLivroPorId() {
        Livro salvo = livroRepository.save(
                new Livro("Livro Teste", "Autor", "Gênero", "Descrição", "img", "user1")
        );

        ResponseEntity<Livro> response = restTemplate.getForEntity(
                url("/api/livros/" + salvo.getId()),
                Livro.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Livro Teste", response.getBody().getTitulo());
    }

    @Test
    void deveRetornar404ParaLivroInexistente() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                url("/api/livros/id-invalido"),
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deveAtualizarLivro() {
        Livro salvo = livroRepository.save(
                new Livro("Antigo", "Autor", "Gênero", "Descrição", "img", "user1")
        );

        Livro atualizado = new Livro(
                "Novo Título",
                "Autor Atualizado",
                "Novo Gênero",
                "Nova descrição",
                "nova-img",
                "user1"
        );

        HttpEntity<Livro> request = new HttpEntity<>(atualizado);

        ResponseEntity<Livro> response = restTemplate.exchange(
                url("/api/livros/" + salvo.getId()),
                HttpMethod.PUT,
                request,
                Livro.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Novo Título", response.getBody().getTitulo());
        assertEquals("Autor Atualizado", response.getBody().getAutor());
    }

    @Test
    void deveRetornar404AoAtualizarLivroInexistente() {
        Livro livro = new Livro(
                "Novo",
                "Autor",
                "Gênero",
                "Descrição",
                "img",
                "user1"
        );

        HttpEntity<Livro> request = new HttpEntity<>(livro);

        ResponseEntity<String> response = restTemplate.exchange(
                url("/api/livros/id-invalido"),
                HttpMethod.PUT,
                request,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deveRetornar400AoAtualizarLivroComTituloInvalido() {
        Livro salvo = livroRepository.save(
                new Livro("Antigo", "Autor", "Gênero", "Descrição", "img", "user1")
        );

        Livro atualizado = new Livro(
                "",
                "Autor",
                "Gênero",
                "Descrição",
                "img",
                "user1"
        );

        HttpEntity<Livro> request = new HttpEntity<>(atualizado);

        ResponseEntity<String> response = restTemplate.exchange(
                url("/api/livros/" + salvo.getId()),
                HttpMethod.PUT,
                request,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void deveDeletarLivro() {
        Livro salvo = livroRepository.save(
                new Livro("Livro", "Autor", "Gênero", "Descrição", "img", "user1")
        );

        ResponseEntity<Void> response = restTemplate.exchange(
                url("/api/livros/" + salvo.getId()),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertTrue(livroRepository.findById(salvo.getId()).isEmpty());
    }

    @Test
    void deveRetornar404AoDeletarLivroInexistente() {
        ResponseEntity<String> response = restTemplate.exchange(
                url("/api/livros/id-invalido"),
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}