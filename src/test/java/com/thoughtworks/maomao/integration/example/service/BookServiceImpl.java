package com.thoughtworks.maomao.integration.example.service;

import com.thoughtworks.maomao.annotation.Service;
import com.thoughtworks.maomao.annotations.Glue;
import com.thoughtworks.maomao.integration.example.model.Book;
import com.thoughtworks.maomao.noam.SessionFactory;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

//    @Glue
    private SessionFactory sessionFactory = new SessionFactory("com.thoughtworks.maomao.integration.example");;

    @Override
    public Book getBook(Integer id) {
        return sessionFactory.from(Book.class).getById(id);
    }

    @Override
    public void updateBook(Book book) {
        sessionFactory.save(book);
    }

    @Override
    public void deleteBook(Integer id) {
        Book book = getBook(id);
        sessionFactory.delete(book);
    }

    @Override
    public List<Book> getAllBooks() {
        return sessionFactory.from(Book.class).list();
    }

    @Override
    public Book addBook(Book book) {
        sessionFactory.save(book);
        return book;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
