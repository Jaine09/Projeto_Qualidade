package com.livraria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.livraria.entity.Usuario;
import com.livraria.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/")
    public String loginPage() {
        return "index";
    }

    @PostMapping("/login")
    public String login(Usuario usuario, HttpSession session) {

        var user = usuarioService.login(usuario.getEmail(), usuario.getSenha());

        if (user.isPresent()) {
            session.setAttribute("usuarioLogado", user.get());
            return "redirect:/home";
        }

        return "index";
    }

    @GetMapping("/cadastro")
    public String cadastroPage() {
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(Usuario usuario) {
        usuarioService.salvar(usuario);
        return "redirect:/";
    }
}