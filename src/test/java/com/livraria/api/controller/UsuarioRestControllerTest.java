package com.livraria.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.livraria.api.repository.UsuarioRepository;
import com.livraria.api.service.UsuarioService;
import com.livraria.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class UsuarioRestControllerTest {

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

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void limparBanco() {
        usuarioRepository.deleteAll();
    }

    @Test
    void deveCriarUsuarioComSucessoESemExporSenha() throws Exception {
        Usuario usuario = new Usuario("Giulia", "giulia@email.com", "senha123");

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Giulia"))
                .andExpect(jsonPath("$.email").value("giulia@email.com"))
                .andExpect(jsonPath("$.senha").doesNotExist());

        assertEquals(1, usuarioRepository.count());
    }

    @Test
    void deveRetornarBadRequestAoCriarUsuarioInvalido() throws Exception {
        Usuario usuario = new Usuario("", "email@email.com", "senha123");

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornarBadRequestAoCriarUsuarioComEmailDuplicado() throws Exception {
        usuarioService.salvar(new Usuario("Giulia", "giulia@email.com", "senha123"));

        Usuario duplicado = new Usuario("Outra", "giulia@email.com", "outraSenha");

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicado)))
                .andExpect(status().isBadRequest());

        assertEquals(1, usuarioRepository.count());
    }

    @Test
    void deveListarUsuariosSemExporSenhas() throws Exception {
        usuarioService.salvar(new Usuario("Giulia", "giulia@email.com", "senha123"));
        usuarioService.salvar(new Usuario("Ana", "ana@email.com", "senha456"));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].senha").doesNotExist())
                .andExpect(jsonPath("$[1].senha").doesNotExist());
    }

    @Test
    void deveBuscarUsuarioPorIdSemExporSenha() throws Exception {
        Usuario salvo = usuarioService.salvar(new Usuario("Giulia", "giulia@email.com", "senha123"));

        mockMvc.perform(get("/api/usuarios/" + salvo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Giulia"))
                .andExpect(jsonPath("$.senha").doesNotExist());
    }

    @Test
    void deveRetornarNotFoundAoBuscarUsuarioInexistente() throws Exception {
        mockMvc.perform(get("/api/usuarios/id-inexistente"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveAtualizarUsuarioComSucesso() throws Exception {
        Usuario salvo = usuarioService.salvar(new Usuario("Giulia", "giulia@email.com", "senha123"));
        Usuario atualizado = new Usuario("Giulia Atualizada", "giulia.novo@email.com", "novaSenha");

        mockMvc.perform(put("/api/usuarios/" + salvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Giulia Atualizada"))
                .andExpect(jsonPath("$.email").value("giulia.novo@email.com"))
                .andExpect(jsonPath("$.senha").doesNotExist());

        Usuario usuarioBanco = usuarioRepository.findById(salvo.getId()).orElseThrow();
        assertEquals("Giulia Atualizada", usuarioBanco.getNome());
        assertEquals("giulia.novo@email.com", usuarioBanco.getEmail());
    }

    @Test
    void deveRetornarBadRequestAoAtualizarUsuarioInvalido() throws Exception {
        Usuario salvo = usuarioService.salvar(new Usuario("Giulia", "giulia@email.com", "senha123"));
        Usuario invalido = new Usuario("", "novo@email.com", "senha123");

        mockMvc.perform(put("/api/usuarios/" + salvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornarNotFoundAoAtualizarUsuarioInexistente() throws Exception {
        Usuario usuario = new Usuario("Giulia", "giulia@email.com", "senha123");

        mockMvc.perform(put("/api/usuarios/id-inexistente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveDeletarUsuarioComSucesso() throws Exception {
        Usuario salvo = usuarioService.salvar(new Usuario("Giulia", "giulia@email.com", "senha123"));

        mockMvc.perform(delete("/api/usuarios/" + salvo.getId()))
                .andExpect(status().isNoContent());

        assertTrue(usuarioRepository.findById(salvo.getId()).isEmpty());
    }

    @Test
    void deveRetornarNotFoundAoDeletarUsuarioInexistente() throws Exception {
        mockMvc.perform(delete("/api/usuarios/id-inexistente"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveFazerLoginComSucessoSemExporSenha() throws Exception {
        usuarioService.salvar(new Usuario("Giulia", "giulia@email.com", "senha123"));

        Usuario login = new Usuario(null, "giulia@email.com", "senha123");

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("giulia@email.com"))
                .andExpect(jsonPath("$.senha").doesNotExist());
    }

    @Test
    void deveRetornarUnauthorizedQuandoLoginInvalido() throws Exception {
        usuarioService.salvar(new Usuario("Giulia", "giulia@email.com", "senha123"));

        Usuario login = new Usuario(null, "giulia@email.com", "senhaErrada");

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Email ou senha inválidos"));
    }
}