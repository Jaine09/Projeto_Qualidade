package com.livraria.api.service;

import com.livraria.entity.Livro;
import com.livraria.api.repository.LivroRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class LivroServiceParametrizedTest {

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

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    void naoDeveSalvarLivroComTituloInvalido(String tituloInvalido) {
        Livro livro = new Livro(
                tituloInvalido,
                "Autor",
                "Gênero",
                "Descrição",
                "img",
                "user1"
        );

        assertThrows(IllegalArgumentException.class, () -> {
            livroService.salvar(livro);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    void naoDeveSalvarLivroComUsuarioInvalido(String usuarioInvalido) {
        Livro livro = new Livro(
                "Título válido",
                "Autor",
                "Gênero",
                "Descrição",
                "img",
                usuarioInvalido
        );

        assertThrows(IllegalArgumentException.class, () -> {
            livroService.salvar(livro);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"user1", "user2", "user3"})
    void deveSalvarLivroParaDiferentesUsuarios(String usuarioId) {
        Livro livro = new Livro(
                "Livro Teste",
                "Autor",
                "Gênero",
                "Descrição",
                "img",
                usuarioId
        );

        Livro salvo = livroService.salvar(livro);

        assertNotNull(salvo.getId());
        assertEquals(usuarioId, salvo.getUsuarioId());
    }
}   