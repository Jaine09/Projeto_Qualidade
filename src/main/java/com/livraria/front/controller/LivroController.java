package com.livraria.front.controller;

import com.livraria.entity.Livro;
import com.livraria.entity.Usuario;
import com.livraria.api.service.LivroService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
public class LivroController {

    @Autowired
    private LivroService livroService;

    private Usuario getUsuarioLogado(HttpSession session) {
        return (Usuario) session.getAttribute("usuarioLogado");
    }

    @GetMapping("/home")
    public String home(Model model, HttpSession session) {
        Usuario usuario = getUsuarioLogado(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("livros", livroService.listarPorUsuario(usuario.getId()));
        return "pagina-inicial";
    }

    @GetMapping("/novo")
    public String novoLivro(Model model, HttpSession session) {
        Usuario usuario = getUsuarioLogado(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("livro", new Livro());
        return "cadastrarLivro";
    }

    @PostMapping("/salvar")
    public String salvar(Livro livro,
                         HttpSession session,
                         Model model) {

        Usuario usuario = getUsuarioLogado(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            livro.setUsuarioId(usuario.getId());
            livroService.salvar(livro);

            return "redirect:/home";

        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("livro", livro);

            return "cadastrarLivro";
        }
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable String id,
                         Model model,
                         HttpSession session) {

        Usuario usuario = getUsuarioLogado(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        return livroService.buscarPorId(id)
                .filter(livro -> livro.getUsuarioId().equals(usuario.getId()))
                .map(livro -> {
                    model.addAttribute("livro", livro);
                    return "editarLivros";
                })
                .orElse("redirect:/home");
    }

    @PostMapping("/editar/{id}")
    public String atualizar(@PathVariable String id,
                            Livro livroAtualizado,
                            HttpSession session,
                            Model model) {

        Usuario usuario = getUsuarioLogado(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            livroService.buscarPorId(id)
                    .filter(livro -> livro.getUsuarioId().equals(usuario.getId()))
                    .ifPresent(livro -> {
                        livroAtualizado.setUsuarioId(usuario.getId());
                        livroService.atualizar(id, livroAtualizado);
                    });

            return "redirect:/home";

        } catch (IllegalArgumentException e) {
            livroAtualizado.setId(id);
            livroAtualizado.setUsuarioId(usuario.getId());

            model.addAttribute("erro", e.getMessage());
            model.addAttribute("livro", livroAtualizado);

            return "editarLivros";
        }
    }

    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable String id,
                          HttpSession session) {

        Usuario usuario = getUsuarioLogado(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        livroService.buscarPorId(id)
                .filter(livro -> livro.getUsuarioId().equals(usuario.getId()))
                .ifPresent(livro -> livroService.deletar(id));

        return "redirect:/home";
    }
}