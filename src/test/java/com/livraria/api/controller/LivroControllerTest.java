package com.livraria.api.controller;

import com.livraria.api.repository.LivroRepository;
import com.livraria.api.repository.UsuarioRepository;
import com.livraria.entity.Livro;
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

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class LivroControllerTest {

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
    private UsuarioRepository usuarioRepository;

    private Usuario usuarioLogado;
    private MockHttpSession sessaoAtiva;

    @BeforeEach
    void configurar() {
        livroRepository.deleteAll();
        usuarioRepository.deleteAll();

        usuarioLogado = usuarioRepository.save(
                new Usuario("Giulia", "giulia@email.com", "senha")
        );

        sessaoAtiva = new MockHttpSession();
        sessaoAtiva.setAttribute("usuarioLogado", usuarioLogado);
    }

    @Test
    void deveExibirHomeComLivrosDoUsuario() throws Exception {
        livroRepository.save(new Livro("Livro 1", "Autor", "Gênero", "Desc", "img", usuarioLogado.getId()));
        livroRepository.save(new Livro("Livro 2", "Autor", "Gênero", "Desc", "img", usuarioLogado.getId()));
        livroRepository.save(new Livro("Livro Outro", "Autor", "Gênero", "Desc", "img", "outro-id"));

        mockMvc.perform(get("/home").session(sessaoAtiva))
                .andExpect(status().isOk())
                .andExpect(view().name("pagina-inicial"))
                .andExpect(model().attributeExists("livros"))
                .andExpect(model().attribute("livros", hasSize(2)));
    }

    @Test
    void deveRedirecionarHomeParaLoginSemSessao() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void deveExibirFormularioNovoLivro() throws Exception {
        mockMvc.perform(get("/novo").session(sessaoAtiva))
                .andExpect(status().isOk())
                .andExpect(view().name("cadastrarLivro"))
                .andExpect(model().attributeExists("livro"));
    }

    @Test
    void deveRedirecionarNovoParaLoginSemSessao() throws Exception {
        mockMvc.perform(get("/novo"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void deveSalvarLivroComSucesso() throws Exception {
        mockMvc.perform(post("/salvar")
                        .session(sessaoAtiva)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("titulo", "Novo Livro")
                        .param("autor", "Autor")
                        .param("genero", "Romance")
                        .param("descricao", "Descrição")
                        .param("imagem", "img"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        assertEquals(1, livroRepository.findByUsuarioId(usuarioLogado.getId()).size());
    }

    @Test
    void deveRetornarErroAoSalvarLivroInvalido() throws Exception {
        mockMvc.perform(post("/salvar")
                        .session(sessaoAtiva)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("titulo", "")
                        .param("autor", "Autor")
                        .param("genero", "Romance")
                        .param("descricao", "Descrição"))
                .andExpect(status().isOk())
                .andExpect(view().name("cadastrarLivro"))
                .andExpect(model().attributeExists("erro"))
                .andExpect(model().attributeExists("livro"));
    }

    @Test
    void deveRedirecionarSalvarParaLoginSemSessao() throws Exception {
        mockMvc.perform(post("/salvar")
                        .param("titulo", "Livro"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void deveExibirTelaEdicao() throws Exception {
        Livro livro = livroRepository.save(
                new Livro("Livro", "Autor", "Gênero", "Desc", "img", usuarioLogado.getId())
        );

        mockMvc.perform(get("/editar/" + livro.getId()).session(sessaoAtiva))
                .andExpect(status().isOk())
                .andExpect(view().name("editarLivros"))
                .andExpect(model().attributeExists("livro"));
    }

    @Test
    void deveRedirecionarAoEditarLivroInexistente() throws Exception {
        mockMvc.perform(get("/editar/id-inexistente").session(sessaoAtiva))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    void deveRedirecionarEditarParaLoginSemSessao() throws Exception {
        mockMvc.perform(get("/editar/id"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void deveAtualizarLivroComSucesso() throws Exception {
        Livro livro = livroRepository.save(
                new Livro("Antigo", "Autor", "Gênero", "Desc", "img", usuarioLogado.getId())
        );

        mockMvc.perform(post("/editar/" + livro.getId())
                        .session(sessaoAtiva)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("titulo", "Novo")
                        .param("autor", "Novo Autor")
                        .param("genero", "Novo Gênero")
                        .param("descricao", "Nova Desc")
                        .param("imagem", "nova-img"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        Livro atualizado = livroRepository.findById(livro.getId()).orElseThrow();
        assertEquals("Novo", atualizado.getTitulo());
    }

    @Test
    void deveRetornarErroAoAtualizarLivroInvalido() throws Exception {
        Livro livro = livroRepository.save(
                new Livro("Antigo", "Autor", "Gênero", "Desc", "img", usuarioLogado.getId())
        );

        mockMvc.perform(post("/editar/" + livro.getId())
                        .session(sessaoAtiva)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("titulo", "")
                        .param("autor", "Autor")
                        .param("genero", "Gênero")
                        .param("descricao", "Desc"))
                .andExpect(status().isOk())
                .andExpect(view().name("editarLivros"))
                .andExpect(model().attributeExists("erro"))
                .andExpect(model().attributeExists("livro"));
    }

    @Test
    void deveRedirecionarAtualizarParaLoginSemSessao() throws Exception {
        mockMvc.perform(post("/editar/id")
                        .param("titulo", "Livro"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void deveDeletarLivroComSucesso() throws Exception {
        Livro livro = livroRepository.save(
                new Livro("Livro", "Autor", "Gênero", "Desc", "img", usuarioLogado.getId())
        );

        mockMvc.perform(post("/deletar/" + livro.getId()).session(sessaoAtiva))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        assertTrue(livroRepository.findById(livro.getId()).isEmpty());
    }

    @Test
    void deveRedirecionarDeletarParaLoginSemSessao() throws Exception {
        mockMvc.perform(post("/deletar/id"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}