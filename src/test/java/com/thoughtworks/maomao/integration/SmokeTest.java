//package com.thoughtworks.maomao.integration;
//
//
//import com.thoughtworks.maomao.integration.example.model.Book;
//import com.thoughtworks.maomao.integration.example.service.BookServiceImpl;
//import org.eclipse.jetty.client.api.ContentResponse;
//import org.eclipse.jetty.client.api.Response;
//import org.junit.Test;
//
//import java.nio.ByteBuffer;
//import java.util.List;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//
//public class SmokeTest extends AbstractWebTest {
//
//    @Test
//    public void should_get_books_info() throws Exception {
//
//        ContentResponse response = client
//                .GET("http://localhost:11090/noam/book?method=index");
//
//        assertEquals(200, response.getStatus());
//        assertNotNull(response.getContent());
//        List<Book> allBooks = new BookServiceImpl().getAllBooks();
//        for (Book book : allBooks) {
//            assertTrue(response.getContentAsString().contains(book.getName()));
//            assertTrue(response.getContentAsString().contains(book.getAuthor()));
//        }
//    }
//
//    @Test
//    public void should_delete_book() throws Exception {
//
//        ContentResponse response = client
//                .GET("http://localhost:11090/noam-mvc/book?id=2&method=delete");
//
//        assertEquals(200, response.getStatus());
//        assertNotNull(response.getContent());
//        List<Book> allBooks = new BookServiceImpl().getAllBooks();
//        for (Book book : allBooks) {
//            assertTrue(response.getContentAsString().contains(book.getName()));
//            assertTrue(response.getContentAsString().contains(book.getAuthor()));
//        }
//    }
//
//    @Test
//    public void should_post_book() throws Exception {
//
//        client.POST("http://localhost:11090/noam-mvc/book?method=create")
//                .param("book.name", "How to build mvc framework?")
//                .param("book.author", "Lei")
//                .param("book.comment.content", "Good book.")
//                .param("book.comment.author", "Lu")
//                .send(new Response.Listener.Empty() {
//                    @Override
//                    public void onContent(Response response, ByteBuffer buffer) {
//                        BookServiceImpl bookService = new BookServiceImpl();
//                        int bookNumber = bookService.getAllBooks().size();
//
//                        ContentResponse contentResponse = (ContentResponse) response;
//                        assertEquals(200, response.getStatus());
//                        assertNotNull(contentResponse.getContent());
//                        Book book = bookService.getBook(++bookNumber);
//                        assertTrue(contentResponse.getContentAsString().contains(book.getName()));
//                        assertTrue(contentResponse.getContentAsString().contains(book.getAuthor()));
//                        assertTrue(contentResponse.getContentAsString().contains(book.getComment().getContent()));
//                        assertTrue(contentResponse.getContentAsString().contains(book.getComment().getAuthor()));   // Your logic here
//                    }
//                });
//
//    }
//}
