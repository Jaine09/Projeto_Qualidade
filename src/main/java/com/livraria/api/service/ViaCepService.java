package com.livraria.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.livraria.entity.ViaCepResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ViaCepService {

    private String baseUrl = "https://viacep.com.br/ws/";

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ViaCepService() {
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public ViaCepService(OkHttpClient httpClient) {
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public ViaCepResponse buscarCep(String cep) throws IOException {

        String cepLimpo = cep.replaceAll("[^0-9]", "");

        if (cepLimpo.length() != 8) {
            throw new IllegalArgumentException("CEP deve conter 8 dígitos");
        }

        String url = baseUrl + cepLimpo + "/json/";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new IOException("Erro ao consultar CEP");
            }

            String responseBody = response.body().string();

            if (responseBody.contains("\"erro\": true")
                    || responseBody.contains("\"erro\":true")
                    || responseBody.contains("\"erro\":\"true\"")
                    || responseBody.contains("\"erro\": \"true\"")) {
                throw new IOException("CEP não encontrado");
            }

            return objectMapper.readValue(responseBody, ViaCepResponse.class);
        }
    }
}