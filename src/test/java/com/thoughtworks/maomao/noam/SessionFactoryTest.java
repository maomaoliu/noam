package com.thoughtworks.maomao.noam;

import com.thoughtworks.maomao.AbstractNoamTest;
import com.thoughtworks.maomao.model.Book;
import com.thoughtworks.maomao.model.Comment;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SessionFactoryTest extends AbstractNoamTest{

    private SessionFactory sessionFactory;

    @Before
    public void setUp() throws Exception {
        sessionFactory = new SessionFactory("com.thoughtworks.maomao.model");
    }

    @Test
    public void should_read_book_list() throws Exception {
        List<Book> books = sessionFactory.from(Book.class).list();
        assertEquals(2, books.size());
        Book book = books.get(0);

        assertEquals("Java Book", book.getName());
        assertEquals("maomao", book.getAuthor());
        assertEquals(12.34, book.getPrice(), 0.001);

        List<Comment> comments = book.getComments();
        assertEquals(2, comments.size());
        assertEquals("nice java book", comments.get(0).getContent());
    }

    @Test
    public void should_add_new_book() throws Exception {
        List<Book> booksBefore = sessionFactory.from(Book.class).list();
        Book book = new Book();
        book.setName("a new book");
        book.setAuthor("anonymous");
        book.setPrice(9.82f);
        sessionFactory.save(book);

        List<Book> booksAfter = sessionFactory.from(Book.class).list();
        assertEquals(1, booksAfter.size() - booksBefore.size());
    }

}
