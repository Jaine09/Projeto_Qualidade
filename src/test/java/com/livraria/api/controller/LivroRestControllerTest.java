package com.livraria.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.livraria.api.repository.LivroRepository;
import com.livraria.entity.Livro;
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
class LivroRestControllerTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void limparBanco() {
        livroRepository.deleteAll();
    }

    @Test
    void deveCriarLivroComSucesso() throws Exception {
        Livro livro = new Livro("Dom Casmurro", "Machado de Assis", "Romance", "Descrição", "imagem.jpg", "usuario-1");

        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livro)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$.autor").value("Machado de Assis"));

        assertEquals(1, livroRepository.count());
    }

    @Test
    void deveRetornarBadRequestAoCriarLivroInvalido() throws Exception {
        Livro livro = new Livro("", "Autor", "Gênero", "Descrição", "img", "usuario-1");

        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livro)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveListarLivrosPorUsuario() throws Exception {
        livroRepository.save(new Livro("Livro 1", "Autor", "Gênero", "Desc", "img", "usuario-1"));
        livroRepository.save(new Livro("Livro 2", "Autor", "Gênero", "Desc", "img", "usuario-1"));
        livroRepository.save(new Livro("Livro 3", "Autor", "Gênero", "Desc", "img", "usuario-2"));

        mockMvc.perform(get("/api/livros/usuario/usuario-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void deveRetornarBadRequestAoListarComUsuarioInvalido() throws Exception {
        mockMvc.perform(get("/api/livros/usuario/%20"))
                .andExpect(status().isBadRequest());
                
    }

    @Test
    void deveBuscarLivroPorId() throws Exception {
        Livro livro = livroRepository.save(new Livro("Livro", "Autor", "Gênero", "Desc", "img", "usuario-1"));

        mockMvc.perform(get("/api/livros/" + livro.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Livro"));
    }

    @Test
    void deveRetornarNotFoundAoBuscarLivroInexistente() throws Exception {
        mockMvc.perform(get("/api/livros/id-inexistente"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Livro não encontrado"));
    }

    @Test
    void deveRetornarBadRequestAoBuscarComIdInvalido() throws Exception {
        mockMvc.perform(get("/api/livros/%20"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveAtualizarLivroComSucesso() throws Exception {
        Livro salvo = livroRepository.save(new Livro("Antigo", "Autor", "Gênero", "Desc", "img", "usuario-1"));
        Livro atualizado = new Livro("Novo", "Novo Autor", "Novo Gênero", "Nova Desc", "nova-img", "usuario-1");

        mockMvc.perform(put("/api/livros/" + salvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Novo"));

        Livro livroBanco = livroRepository.findById(salvo.getId()).orElseThrow();
        assertEquals("Novo", livroBanco.getTitulo());
    }

    @Test
    void deveRetornarBadRequestAoAtualizarLivroInvalido() throws Exception {
        Livro salvo = livroRepository.save(new Livro("Antigo", "Autor", "Gênero", "Desc", "img", "usuario-1"));
        Livro invalido = new Livro("", "Autor", "Gênero", "Desc", "img", "usuario-1");

        mockMvc.perform(put("/api/livros/" + salvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornarNotFoundAoAtualizarLivroInexistente() throws Exception {
        Livro livro = new Livro("Livro", "Autor", "Gênero", "Desc", "img", "usuario-1");

        mockMvc.perform(put("/api/livros/id-inexistente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livro)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Livro não encontrado"));
    }

    @Test
    void deveRetornarBadRequestAoAtualizarComIdInvalido() throws Exception {
        Livro livro = new Livro("Livro", "Autor", "Gênero", "Desc", "img", "usuario-1");

        mockMvc.perform(put("/api/livros/%20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livro)))
                .andExpect(status().isBadRequest());
                
    }

    @Test
    void deveDeletarLivroComSucesso() throws Exception {
        Livro livro = livroRepository.save(new Livro("Livro", "Autor", "Gênero", "Desc", "img", "usuario-1"));

        mockMvc.perform(delete("/api/livros/" + livro.getId()))
                .andExpect(status().isNoContent());

        assertTrue(livroRepository.findById(livro.getId()).isEmpty());
    }

    @Test
    void deveRetornarNotFoundAoDeletarLivroInexistente() throws Exception {
        mockMvc.perform(delete("/api/livros/id-inexistente"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Livro não encontrado"));
    }

    @Test
    void deveRetornarBadRequestAoDeletarComIdInvalido() throws Exception {
        mockMvc.perform(delete("/api/livros/%20"))
                .andExpect(status().isBadRequest());
              
    }
}