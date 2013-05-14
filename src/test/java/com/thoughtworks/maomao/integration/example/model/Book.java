package com.thoughtworks.maomao.integration.example.model;

import com.thoughtworks.maomao.noam.annotation.Column;
import com.thoughtworks.maomao.noam.annotation.Model;

import java.util.List;

@Model
public class Book {
    @Column
    private Integer id;
    @Column
    private String name;
    @Column
    private String author;

    private List<Comment> comments;

    public Book() {
        name = "";
        author = "";
    }

    public Book(String name, String author) {
        this.name = name;
        this.author = author;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
