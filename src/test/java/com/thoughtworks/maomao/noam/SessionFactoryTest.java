package com.thoughtworks.maomao.noam;

import com.thoughtworks.maomao.AbstractNoamTest;
import com.thoughtworks.maomao.model.Book;
import com.thoughtworks.maomao.model.Comment;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SessionFactoryTest extends AbstractNoamTest{
    @Test
    public void should_read_book_list() throws SQLException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        SessionFactory sessionFactory = new SessionFactory("com.thoughtworks.maomao.model");
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
}
