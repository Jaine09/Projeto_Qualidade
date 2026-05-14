package com.livraria.api.service;

import com.livraria.entity.ViaCepResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ViaCepServiceVcrTest {

    private MockWebServer mockWebServer;
    private ViaCepService viaCepService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        viaCepService = new ViaCepService();
        viaCepService.setBaseUrl(mockWebServer.url("/").toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void deveBuscarCepComSucesso() throws IOException {

        String respostaJson = Files.readString(
                Paths.get("src/test/resources/vcr/cep_valido.json"));

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(respostaJson)
                .addHeader("Content-Type", "application/json"));

        ViaCepResponse resposta =
                viaCepService.buscarCep("01001000");

        assertNotNull(resposta);

        assertEquals("01001-000", resposta.getCep());
        assertEquals("Praça da Sé", resposta.getLogradouro());
        assertEquals("São Paulo", resposta.getLocalidade());
    }

    @Test
    void deveRetornarErroParaCepInvalido() throws IOException {

        String respostaJson = Files.readString(
                Paths.get("src/test/resources/vcr/cep_invalido.json"));

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(respostaJson)
                .addHeader("Content-Type", "application/json"));

        Exception exception = assertThrows(
                IOException.class,
                () -> viaCepService.buscarCep("00000000")
        );

        assertEquals(
                "CEP não encontrado",
                exception.getMessage()
        );
    }
}