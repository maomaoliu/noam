package com.thoughtworks.maomao.integration.example.service;

import com.thoughtworks.maomao.annotation.Service;
import com.thoughtworks.maomao.annotations.Glue;
import com.thoughtworks.maomao.integration.example.model.Comment;
import com.thoughtworks.maomao.noam.SessionFactory;

import java.util.HashMap;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService{

    private SessionFactory sessionFactory;

    @Glue
    public CommentServiceImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Comment addComment(Comment comment) {
        Map<String, Object> map = new HashMap<>();
        map.put("book_id", comment.getBook().getId());
        sessionFactory.save(comment, map);
        return comment;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
