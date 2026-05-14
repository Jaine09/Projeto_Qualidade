package com.livraria.api.controller;

import com.livraria.entity.ViaCepResponse;
import com.livraria.api.service.ViaCepService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cep")
public class CepController {

    @Autowired
    private ViaCepService viaCepService;

    @GetMapping("/{cep}")
    public ViaCepResponse buscarCep(
            @PathVariable String cep
    ) throws Exception {

        return viaCepService.buscarCep(cep);
    }
}