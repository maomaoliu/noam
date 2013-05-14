package com.thoughtworks.maomao.integration.example.service;

import com.thoughtworks.maomao.annotation.Service;
import com.thoughtworks.maomao.integration.example.model.Comment;
import com.thoughtworks.maomao.noam.SessionFactory;

import java.util.HashMap;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService{
    private SessionFactory sessionFactory = new SessionFactory("com.thoughtworks.maomao.integration.example");;
    @Override
    public Comment addComment(Comment comment) {
        Map<String, Object> map = new HashMap<>();
        map.put("book_id", comment.getBook().getId());
        sessionFactory.save(comment, map);
        return comment;
    }
}
