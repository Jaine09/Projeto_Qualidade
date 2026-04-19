package com.livraria.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "livros")
public class Livro {

    @Id
    private String id;

    private String titulo;
    private String autor;
    private String genero;
    private String descricao;
    private String imagem;

    private String usuarioId; // referência ao usuário

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Livro() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Livro(String titulo, String autor, String genero, String descricao, String imagem, String usuarioId) {
        this();
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.descricao = descricao;
        this.imagem = imagem;
        this.usuarioId = usuarioId;
    }

    // Getters e Setters
    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo; }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
        this.updatedAt = LocalDateTime.now();
    }

    public String getAutor() { return autor; }

    public void setAutor(String autor) {
        this.autor = autor;
        this.updatedAt = LocalDateTime.now();
    }

    public String getGenero() { return genero; }

    public void setGenero(String genero) {
        this.genero = genero;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDescricao() { return descricao; }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
        this.updatedAt = LocalDateTime.now();
    }

    public String getImagem() { return imagem; }

    public void setImagem(String imagem) {
        this.imagem = imagem;
        this.updatedAt = LocalDateTime.now();
    }

    public String getUsuarioId() { return usuarioId; }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
}