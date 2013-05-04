package com.thoughtworks.maomao.noam;

import com.thoughtworks.maomao.model.Book;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ModelInfoTest {

    private ModelInfo modelInfo;

    @Before
    public void setUp() throws Exception {
        modelInfo = new ModelInfo(Book.class);
    }

    @Test
    public void should_get_correct_table_name() {
        assertEquals("BOOK", modelInfo.getTableName());
    }

    @Test
    public void should_get_correct_columns() {
        List<String> columns = modelInfo.getColumns();
        assertEquals(4, columns.size());
        assertEquals("id", columns.get(0));
        assertEquals("name", columns.get(1));
        assertEquals("author", columns.get(2));
        assertEquals("price", columns.get(3));
    }
}
