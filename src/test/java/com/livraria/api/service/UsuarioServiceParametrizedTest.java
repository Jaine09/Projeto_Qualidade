package com.livraria.api.service;

import com.livraria.api.repository.UsuarioRepository;
import com.livraria.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
public class UsuarioServiceParametrizedTest {

    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.data.mongodb.uri",
                mongoDBContainer::getReplicaSetUrl
        );
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

        Usuario usuario = new Usuario(
                nomeInvalido,
                "email@email.com",
                "123456",
                "01001000",
                "Rua A",
                "São Paulo",
                "SP"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.salvar(usuario)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    void naoDeveSalvarUsuarioComEmailInvalido(String emailInvalido) {

        Usuario usuario = new Usuario(
                "Nome",
                emailInvalido,
                "123456",
                "01001000",
                "Rua A",
                "São Paulo",
                "SP"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.salvar(usuario)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    void naoDeveSalvarUsuarioComSenhaInvalida(String senhaInvalida) {

        Usuario usuario = new Usuario(
                "Nome",
                "email@email.com",
                senhaInvalida,
                "01001000",
                "Rua A",
                "São Paulo",
                "SP"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.salvar(usuario)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    void naoDeveSalvarUsuarioComCepInvalido(String cepInvalido) {

        Usuario usuario = new Usuario(
                "Nome",
                "email@email.com",
                "123456",
                cepInvalido,
                "Rua A",
                "São Paulo",
                "SP"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.salvar(usuario)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    void naoDeveSalvarUsuarioComEnderecoInvalido(String enderecoInvalido) {

        Usuario usuario = new Usuario(
                "Nome",
                "email@email.com",
                "123456",
                "01001000",
                enderecoInvalido,
                "São Paulo",
                "SP"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.salvar(usuario)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    void naoDeveSalvarUsuarioComCidadeInvalida(String cidadeInvalida) {

        Usuario usuario = new Usuario(
                "Nome",
                "email@email.com",
                "123456",
                "01001000",
                "Rua A",
                cidadeInvalida,
                "SP"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.salvar(usuario)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    void naoDeveSalvarUsuarioComEstadoInvalido(String estadoInvalido) {

        Usuario usuario = new Usuario(
                "Nome",
                "email@email.com",
                "123456",
                "01001000",
                "Rua A",
                "São Paulo",
                estadoInvalido
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.salvar(usuario)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"errada", "1234", "senha"})
    void naoDeveFazerLoginComSenhasInvalidas(String senhaErrada) {

        usuarioService.salvar(new Usuario(
                "Nome",
                "email@email.com",
                "123456",
                "01001000",
                "Rua A",
                "São Paulo",
                "SP"
        ));

        var resultado = usuarioService.login(
                "email@email.com",
                senhaErrada
        );

        assertTrue(resultado.isEmpty());
    }
}
