package com.livraria.api.service;

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

        String resposta = viaCepService.buscarCep("01001000");

        assertNotNull(resposta);
        assertTrue(resposta.contains("01001-000"));
        assertTrue(resposta.contains("Praça da Sé"));
        assertTrue(resposta.contains("São Paulo"));
    }
    @Test
void deveRetornarErroParaCepInvalido() throws IOException {

    String respostaJson = Files.readString(
            Paths.get("src/test/resources/vcr/cep_invalido.json"));

    mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .setBody(respostaJson)
            .addHeader("Content-Type", "application/json"));

    String resposta = viaCepService.buscarCep("00000000");

    assertNotNull(resposta);
    assertTrue(resposta.contains("erro"));
}
}