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
            statement.execute("DROP TABLE COMMENT");
        } catch(SQLException sqle) {
            System.out.println("Table not found, not dropping");
        }
        statement.execute("CREATE TABLE BOOK (ID INT PRIMARY KEY AUTO_INCREMENT, NAME VARCHAR(64), AUTHOR VARCHAR(64), PRICE FLOAT)");
        statement.execute("CREATE TABLE COMMENT (ID INT PRIMARY KEY AUTO_INCREMENT, BOOK_ID INT, CONTENT VARCHAR(255), FOREIGN KEY(BOOK_ID) REFERENCES Book(ID))");

        statement.execute("INSERT INTO BOOK (NAME, AUTHOR, PRICE) VALUES('Java Book', 'maomao', 12.34)");
        statement.execute("INSERT INTO BOOK (NAME, AUTHOR, PRICE) VALUES('Ruby book', 'oamoam', 9.82)");

        statement.execute("INSERT INTO COMMENT (BOOK_ID, CONTENT) VALUES(1, 'nice java book')");
        statement.execute("INSERT INTO COMMENT (BOOK_ID, CONTENT) VALUES(1, 'a little boring')");
        statement.execute("INSERT INTO COMMENT (BOOK_ID, CONTENT) VALUES(2, 'worth reading, recommended')");

        statement.close();
        connection.close();
    }
}
