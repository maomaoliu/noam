package com.thoughtworks.maomao.noam;

import com.thoughtworks.maomao.AbstractNoamTest;
import com.thoughtworks.maomao.model.Book;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SessionFactoryTest extends AbstractNoamTest{
    @Test
    public void should_read_book_list() throws SQLException {
        SessionFactory sessionFactory = new SessionFactory("com.thoughtworks.maomao.model");
        List<Book> books = sessionFactory.from(Book.class).list();
        assertEquals(2, books.size());
        assertEquals("Java Book", books.get(0).getName());
        assertEquals("maomao", books.get(0).getAuthor());
        assertEquals(12.34, books.get(0).getPrice(), 0.001);
    }
}
