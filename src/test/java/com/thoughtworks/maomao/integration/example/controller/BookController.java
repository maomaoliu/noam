package com.thoughtworks.maomao.integration.example.controller;


import com.thoughtworks.maomao.annotation.Controller;
import com.thoughtworks.maomao.annotation.Param;
import com.thoughtworks.maomao.annotations.Glue;
import com.thoughtworks.maomao.integration.example.model.Book;
import com.thoughtworks.maomao.integration.example.service.BookService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class BookController{

    @Glue
    private BookService bookService;

    public Map index() {
        List books = bookService.getAllBooks();
        HashMap map = new HashMap();
        map.put("books", books);
        return map;
    }

    public Map show(@Param(value = "id") Integer id) {
        Book book = bookService.getBook(id);
        HashMap map = new HashMap();
        map.put("book", book);
        return map;
    }

    public String delete(@Param(value = "id") Integer id) {
        bookService.deleteBook(id);
        return "book?method=index";
    }

    public Map create() {
        Book book = new Book();
        HashMap map = new HashMap();
        map.put("book", book);
        return map;
    }

    public String createPost(@Param(value = "book") Book book) {
        book = bookService.addBook(book);
        return "book?method=show&id="+book.getId();
    }

    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }
}
