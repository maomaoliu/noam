package com.thoughtworks.maomao.orm.model;

import com.thoughtworks.maomao.noam.annotation.Column;
import com.thoughtworks.maomao.noam.annotation.Model;

@Model
public class Comment {
    @Column
    private Integer id;
    @Column
    private String content;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
