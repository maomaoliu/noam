package com.thoughtworks.maomao;

import org.junit.BeforeClass;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class AbstractNoamTest {

    @BeforeClass
    public static void initDB() throws Exception{
//        Server server = Server.createTcpServer("-trace", "-tcp", "-web", "-webPort", "7786").start();
        Class.forName("org.h2.Driver");
        Connection connection = DriverManager.
                getConnection("jdbc:h2:~/test", "sa", "");

        Statement statement = connection.createStatement();
        try {
            statement.execute("DROP TABLE BOOK");
        } catch(SQLException sqle) {
            System.out.println("Table not found, not dropping");
        }
        statement.execute("CREATE TABLE BOOK (ID INT PRIMARY KEY AUTO_INCREMENT, NAME VARCHAR(64), AUTHOR VARCHAR(64), PRICE FLOAT)");
        statement.execute("INSERT INTO BOOK (NAME, AUTHOR, PRICE) VALUES('Java Book', 'maomao', 12.34)");
        statement.execute("INSERT INTO BOOK (NAME, AUTHOR, PRICE) VALUES('Ruby book', 'oamoam', 9.82)");

        statement.close();
        connection.close();
    }
}
