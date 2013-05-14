package com.thoughtworks.maomao.integration.example;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DBInit {
    private static Connection connection;

    public static void connect() throws Exception {
        Class.forName("org.h2.Driver");
        connection = DriverManager.
                getConnection("jdbc:h2:~/test;USER=sa");
    }

    public void executeSqls() throws SQLException {
        Statement statement = connection.createStatement();

        for(String sql: getSqls()){
            statement.execute(sql);
        };

        statement.close();
    }

    public List<String> getSqls(){
        File sqlFile = new File(getClass().getResource("/com/thoughtworks/maomao/integration/example/create.sql").getFile());
        if (sqlFile.exists()) {
            List<String> sqls = null;
            try {
                sqls = Files.readLines(sqlFile, Charsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sqls;
        }
        return null;
    }

    public static void close() throws SQLException {
        connection.close();
    }
}
