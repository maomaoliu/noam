package com.thoughtworks.maomao.integration.example.controller;

import com.thoughtworks.maomao.annotation.Controller;
import com.thoughtworks.maomao.annotation.Param;
import com.thoughtworks.maomao.annotations.Glue;
import com.thoughtworks.maomao.integration.example.model.Book;
import com.thoughtworks.maomao.integration.example.model.Comment;
import com.thoughtworks.maomao.integration.example.service.CommentService;

import java.util.HashMap;
import java.util.Map;

@Controller
public class CommentController {
    @Glue
    private CommentService commentService;

    public Map create(@Param(value = "book_id") Integer bookId) {
        System.out.println("###" + bookId);
        Comment comment = new Comment();
        HashMap map = new HashMap();
        map.put("comment", comment);
        map.put("book_id", bookId);
        return map;
    }

    public String createPost(@Param(value = "comment") Comment comment, @Param(value = "book_id") Integer bookId) {
        Book book = new Book();
        book.setId(bookId);
        comment.setBook(book);
        System.out.println("CreatePost");
        comment = commentService.addComment(comment);
        return "book?method=show&id=" + comment.getBook().getId();
    }

    public void setCommentService(CommentService commentService) {
        this.commentService = commentService;
    }
}
