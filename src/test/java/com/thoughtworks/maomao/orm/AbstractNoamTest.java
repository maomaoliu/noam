package com.thoughtworks.maomao.orm;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public abstract class AbstractNoamTest {

    private static Connection connection;

    @BeforeClass
    public static void initDB() throws Exception {
        Class.forName("org.h2.Driver");
        connection = DriverManager.
                getConnection("jdbc:h2:~/test", "sa", "");
    }

    public void setUp() throws SQLException {
        Statement statement = connection.createStatement();
        try {
            File sqlFile = new File(getClass().getResource("/com/thoughtworks/maomao/orm/create.sql").getFile());
            if (sqlFile.exists()) {
                List<String> sqls = Files.readLines(sqlFile, Charsets.UTF_8);
                for (String sql : sqls) {
                    statement.execute(sql);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        statement.close();
    }

    @AfterClass
    public static void close() throws SQLException {
        connection.close();
    }
}
