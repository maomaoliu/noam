package com.thoughtworks.maomao.integration;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public abstract class AbstractWebTest {

    private static Connection connection;
    private Server server;
    HttpClient client;

    @BeforeClass
    public static void initDB() throws Exception {
        Class.forName("org.h2.Driver");
        connection = DriverManager.
                getConnection("jdbc:h2:~/test", "sa", "");
    }

    public void setUp() throws SQLException {
        Statement statement = connection.createStatement();

        for(String sql: getSqls()){
            statement.execute(sql);
        };

        statement.close();
    }

    public List<String> getSqls(){
            File sqlFile = new File(getClass().getResource("/com/thoughtworks/maomao/integration/create.sql").getFile());
            System.out.println(sqlFile.getAbsolutePath());
            if (sqlFile.exists()) {
                List<String> sqls = null;
                try {
                    sqls = Files.readLines(sqlFile, Charsets.UTF_8);
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                return sqls;
            }
        return null;
    }

    @AfterClass
    public static void close() throws SQLException {
        connection.close();
    }


    @Before
    public void startServer() throws Exception {
        server = new Server(11090);

        String webAppPath = "src/test/webapp";
        String contextPath = "/noam";
        WebAppContext context = new WebAppContext();
        context.setResourceBase(webAppPath);
        context.setDescriptor(webAppPath + "/web.xml");
        context.setContextPath(contextPath);
        context.setParentLoaderPriority(true);

        server.setHandler(context);
        server.start();

        client = new HttpClient();
        client.start();
    }

    @After
    public void stopServer() throws Exception {
        if (client != null)
            client.stop();
        if (server != null)
            server.stop();
        server = null;
    }

}
