package com.livraria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.livraria.entity.Livro;
import com.livraria.entity.Usuario;
import com.livraria.service.LivroService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LivroController {

    @Autowired
    private LivroService livroService;

    @GetMapping("/home")
    public String home(Model model, HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return "redirect:/";
        }

        model.addAttribute("livros", livroService.listarPorUsuario(usuario.getId()));

        return "pagina-inicial";
    }

    @GetMapping("/novo")
    public String novoLivro(Model model) {
        model.addAttribute("livro", new Livro());
        return "cadastrarLivro";
    }

    @PostMapping("/salvar")
    public String salvar(Livro livro, HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return "redirect:/";
        }

        livro.setUsuarioId(usuario.getId());
        livroService.salvar(livro);

        return "redirect:/home";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable String id, Model model) {

        model.addAttribute("livro", livroService.buscarPorId(id));

        return "editarLivros";
    }

    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable String id) {

        livroService.deletar(id);

        return "redirect:/home";
    }
}