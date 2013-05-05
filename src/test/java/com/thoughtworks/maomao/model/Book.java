package com.thoughtworks.maomao.model;

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
    @Column
    private Float price;

    private List<Comment> comments;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
