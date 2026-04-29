package com.livraria.api.service;

import com.livraria.entity.Usuario;
import com.livraria.api.repository.UsuarioRepository;

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
public class UsuarioServiceParametrizedTest {

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

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    void naoDeveSalvarUsuarioComNomeInvalido(String nomeInvalido) {
        Usuario usuario = new Usuario(nomeInvalido, "email@email.com", "123");

        assertThrows(IllegalArgumentException.class, () -> usuarioService.salvar(usuario));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    void naoDeveSalvarUsuarioComEmailInvalido(String emailInvalido) {
        Usuario usuario = new Usuario("Nome", emailInvalido, "123");

        assertThrows(IllegalArgumentException.class, () -> usuarioService.salvar(usuario));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    void naoDeveSalvarUsuarioComSenhaInvalida(String senhaInvalida) {
        Usuario usuario = new Usuario("Nome", "email@email.com", senhaInvalida);

        assertThrows(IllegalArgumentException.class, () -> usuarioService.salvar(usuario));
    }

    @ParameterizedTest
    @ValueSource(strings = {"errada", "1234", "senha"})
    void naoDeveFazerLoginComSenhasInvalidas(String senhaErrada) {
        usuarioService.salvar(new Usuario("Nome", "email@email.com", "123"));

        var resultado = usuarioService.login("email@email.com", senhaErrada);

        assertTrue(resultado.isEmpty());
    }
}