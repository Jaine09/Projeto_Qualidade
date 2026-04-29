package com.livraria.front.controller;

import com.livraria.entity.Usuario;
import com.livraria.api.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping({"/", "/login"})
    public String loginPage(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "index";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute Usuario usuario,
                        HttpSession session,
                        Model model) {

        return usuarioService.login(
                usuario.getEmail(),
                usuario.getSenha()
        ).map(usuarioLogado -> {

            usuarioLogado.setSenha(null);
            session.setAttribute("usuarioLogado", usuarioLogado);

            return "redirect:/home";

        }).orElseGet(() -> {

            model.addAttribute("erro", "Email ou senha inválidos");
            model.addAttribute("usuario", new Usuario());

            return "index";
        });
    }

    @GetMapping("/cadastro")
    public String cadastroPage(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(@ModelAttribute Usuario usuario,
                            Model model) {

        try {
            usuarioService.salvar(usuario);
            return "redirect:/login";

        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("usuario", usuario);
            return "cadastro";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}