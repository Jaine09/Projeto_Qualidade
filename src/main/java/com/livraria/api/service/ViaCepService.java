package com.livraria.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ViaCepService {

    @Value("${viacep.base-url:https://viacep.com.br/ws/}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String buscarCep(String cep) {

        String url = baseUrl + cep + "/json/";

        return restTemplate.getForObject(url, String.class);
    }
}