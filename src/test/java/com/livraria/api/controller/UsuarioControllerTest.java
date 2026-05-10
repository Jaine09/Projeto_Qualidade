package com.livraria.api.controller;

import com.livraria.api.repository.UsuarioRepository;
import com.livraria.api.service.UsuarioService;
import com.livraria.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class UsuarioControllerTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @BeforeEach
    void limparBanco() {
        usuarioRepository.deleteAll();
    }

    @Test
    void deveExibirLoginNaRaiz() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("usuario"));
    }

    @Test
    void deveExibirLoginNaRotaLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("usuario"));
    }

    @Test
    void deveFazerLoginComSucesso() throws Exception {
        usuarioService.salvar(new Usuario("Giulia", "giulia@email.com", "senha123"));

        MockHttpSession sessao = new MockHttpSession();

        mockMvc.perform(post("/login")
                        .session(sessao)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "giulia@email.com")
                        .param("senha", "senha123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        assertNotNull(sessao.getAttribute("usuarioLogado"));
    }

    @Test
    void deveRetornarErroAoFazerLoginInvalido() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "errado@email.com")
                        .param("senha", "senha"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("erro"))
                .andExpect(model().attributeExists("usuario"));
    }

    @Test
    void deveExibirPaginaCadastro() throws Exception {
        mockMvc.perform(get("/cadastro"))
                .andExpect(status().isOk())
                .andExpect(view().name("cadastro"))
                .andExpect(model().attributeExists("usuario"));
    }

    @Test
    void deveCadastrarUsuarioComSucesso() throws Exception {
        mockMvc.perform(post("/cadastro")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("nome", "Giulia")
                        .param("email", "giulia@email.com")
                        .param("senha", "senha123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        assertTrue(usuarioRepository.findByEmail("giulia@email.com").isPresent());
    }

    @Test
    void deveRetornarErroAoCadastrarUsuarioInvalido() throws Exception {
        mockMvc.perform(post("/cadastro")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("nome", "")
                        .param("email", "giulia@email.com")
                        .param("senha", "senha123"))
                .andExpect(status().isOk())
                .andExpect(view().name("cadastro"))
                .andExpect(model().attributeExists("erro"))
                .andExpect(model().attributeExists("usuario"));
    }

    @Test
    void deveFazerLogoutERedirecionarParaLogin() throws Exception {
        MockHttpSession sessao = new MockHttpSession();
        sessao.setAttribute("usuarioLogado", new Usuario("Giulia", "giulia@email.com", null));

        mockMvc.perform(get("/logout").session(sessao))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        assertTrue(sessao.isInvalid());
    }
}
