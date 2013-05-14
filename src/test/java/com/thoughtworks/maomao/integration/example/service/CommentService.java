package com.thoughtworks.maomao.integration.example.service;

import com.thoughtworks.maomao.integration.example.model.Comment;

public interface CommentService {
    public Comment addComment(Comment comment);

    public void deleteComment(Integer id);
}
