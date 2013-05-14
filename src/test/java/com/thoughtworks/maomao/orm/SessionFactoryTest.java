package com.thoughtworks.maomao.orm;

import com.thoughtworks.maomao.noam.SessionFactory;
import com.thoughtworks.maomao.orm.model.Book;
import com.thoughtworks.maomao.orm.model.Comment;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class SessionFactoryTest extends AbstractNoamTest {

    private SessionFactory sessionFactory;

    @Before
    public void setUp() throws SQLException {
        super.setUp();
        sessionFactory = new SessionFactory("com.thoughtworks.maomao.orm.model", "org.h2.Driver", "jdbc:h2:~/test;USER=sa");
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
    public void should_get_book_by_id() throws Exception {
        Book book = sessionFactory.from(Book.class).getById(1);

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

        Comment comment = new Comment();
        comment.setContent("god");
        book.setComments(Arrays.asList(comment));

        sessionFactory.save(book);

        List<Book> booksAfter = sessionFactory.from(Book.class).list();
        assertEquals(1, booksAfter.size() - booksBefore.size());
        assertNotNull(book.getId());
        Book newBook = booksAfter.get(booksAfter.size() - 1);
        assertEquals(1, newBook.getComments().size());
        assertEquals("god", newBook.getComments().get(0).getContent());
    }

    @Test
    public void should_update_book() throws Exception {
        Book book = sessionFactory.from(Book.class).getById(1);
        assertEquals("Java Book", book.getName());
        assertEquals("maomao", book.getAuthor());
        assertEquals(12.34, book.getPrice(), 0.001);

        book.setName("Java Book 2");
        book.setAuthor("maomao liu");
        book.setPrice(21.65f);
        sessionFactory.save(book);

        Book newBook = sessionFactory.from(Book.class).getById(1);
        assertEquals("Java Book 2", newBook.getName());
        assertEquals("maomao liu", newBook.getAuthor());
        assertEquals(21.65, newBook.getPrice(), 0.001);
    }

    @Test
    public void should_delete_book() throws SQLException {
        Book book = sessionFactory.from(Book.class).getById(1);
        assertEquals("Java Book", book.getName());
        List<Comment> comments = book.getComments();
        assertEquals(2, comments.size());

        int result = sessionFactory.delete(book);

        assertEquals(1, result);
        assertNull(sessionFactory.from(Book.class).getById(1));
        for (Comment comment : comments) {
            assertNull(sessionFactory.from(Comment.class).getById(comment.getId()));
        }
    }

    @Test
    public void should_avoid_n_plus_1() throws SQLException {
        List<Book> books = sessionFactory.from(Book.class).where("price > 1").list();
        assertEquals(2, books.size());
        for (Book book : books) {
            List<Comment> comments = book.getComments();
            assertEquals(2, comments.size());
            break;
        }

        Comment comment = sessionFactory.from(Comment.class).getById(3);
        sessionFactory.delete(comment);

        assertEquals((Integer)3, books.get(1).getComments().get(0).getId());;
    }
}
