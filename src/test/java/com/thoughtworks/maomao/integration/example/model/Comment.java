package com.thoughtworks.maomao.integration.example.model;

import com.thoughtworks.maomao.noam.annotation.Column;
import com.thoughtworks.maomao.noam.annotation.Model;

@Model
public class Comment {
    private Book book;
    @Column
    private Integer id;
    @Column
    private String content;
    @Column
    private String author;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
