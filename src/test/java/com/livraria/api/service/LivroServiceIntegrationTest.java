package com.livraria.api.service;

import com.livraria.entity.Livro;
import com.livraria.api.repository.LivroRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class LivroServiceIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private LivroService livroService;

    @Autowired
    private LivroRepository livroRepository;

    @BeforeEach
    void limparBanco() {
        livroRepository.deleteAll();
    }

    // =========================
    // CREATE
    // =========================

    @Test
    void deveSalvarLivroComSucesso() {
        Livro livro = new Livro(
                "Dom Casmurro",
                "Machado de Assis",
                "Romance",
                "Clássico brasileiro",
                "img.jpg",
                "user1"
        );

        Livro salvo = livroService.salvar(livro);

        assertNotNull(salvo.getId());
        assertEquals("Dom Casmurro", salvo.getTitulo());
    }

    @Test
    void naoDeveSalvarLivroSemTitulo() {
        Livro livro = new Livro(
                "", "Autor", "Gênero", "Desc", "img", "user1"
        );

        assertThrows(IllegalArgumentException.class, () -> {
            livroService.salvar(livro);
        });
    }

    @Test
    void naoDeveSalvarLivroSemUsuario() {
        Livro livro = new Livro(
                "Titulo", "Autor", "Gênero", "Desc", "img", ""
        );

        assertThrows(IllegalArgumentException.class, () -> {
            livroService.salvar(livro);
        });
    }

    // =========================
    // READ
    // =========================

    @Test
    void deveListarLivrosPorUsuario() {
        livroService.salvar(new Livro("Livro 1", "Autor", "Gênero", "Desc", "img", "user1"));
        livroService.salvar(new Livro("Livro 2", "Autor", "Gênero", "Desc", "img", "user1"));
        livroService.salvar(new Livro("Livro 3", "Autor", "Gênero", "Desc", "img", "user2"));

        List<Livro> livros = livroService.listarPorUsuario("user1");

        assertEquals(2, livros.size());
    }

    @Test
    void deveRetornarListaVaziaQuandoUsuarioNaoTemLivros() {
        List<Livro> livros = livroService.listarPorUsuario("user-inexistente");

        assertTrue(livros.isEmpty());
    }

    @Test
    void deveBuscarLivroPorId() {
        Livro salvo = livroService.salvar(new Livro(
                "Livro Teste", "Autor", "Gênero", "Desc", "img", "user1"
        ));

        Optional<Livro> encontrado = livroService.buscarPorId(salvo.getId());

        assertTrue(encontrado.isPresent());
        assertEquals("Livro Teste", encontrado.get().getTitulo());
    }

    @Test
    void naoDeveEncontrarLivroComIdInvalido() {
        Optional<Livro> encontrado = livroService.buscarPorId("id-invalido");

        assertTrue(encontrado.isEmpty());
    }

    // =========================
    // UPDATE
    // =========================

    @Test
    void deveAtualizarLivro() {
        Livro salvo = livroService.salvar(new Livro(
                "Antigo", "Autor", "Gênero", "Desc", "img", "user1"
        ));

        Livro atualizado = new Livro(
                "Novo Título", "Autor", "Gênero", "Desc", "img", "user1"
        );

        Optional<Livro> resultado = livroService.atualizar(salvo.getId(), atualizado);

        assertTrue(resultado.isPresent());
        assertEquals("Novo Título", resultado.get().getTitulo());
    }

    @Test
    void naoDeveAtualizarLivroInexistente() {
        Livro livro = new Livro(
                "Novo", "Autor", "Gênero", "Desc", "img", "user1"
        );

        Optional<Livro> resultado = livroService.atualizar("id-invalido", livro);

        assertTrue(resultado.isEmpty());
    }

    // =========================
    // DELETE
    // =========================

    @Test
    void deveDeletarLivro() {
        Livro salvo = livroService.salvar(new Livro(
                "Livro", "Autor", "Gênero", "Desc", "img", "user1"
        ));

        boolean deletado = livroService.deletar(salvo.getId());

        assertTrue(deletado);
        assertFalse(livroRepository.findById(salvo.getId()).isPresent());
    }

    @Test
    void naoDeveDeletarLivroInexistente() {
        boolean resultado = livroService.deletar("id-invalido");

        assertFalse(resultado);
    }
}