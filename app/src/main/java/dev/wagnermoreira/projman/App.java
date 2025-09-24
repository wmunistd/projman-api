package dev.wagnermoreira.projman;

import dev.wagnermoreira.projman.dao.JdbcUserDao;
import dev.wagnermoreira.projman.dao.UserDao;
import dev.wagnermoreira.projman.db.Database;
import dev.wagnermoreira.projman.api.ApiServer;

import java.util.List;

public class App {
    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        System.out.println(new App().getGreeting());

        Database.initSchema();

        UserDao userDao = new JdbcUserDao();

        try {
            ApiServer server = new ApiServer(8080, userDao);
            server.start();
            System.out.println("API running on http://localhost:8080 (GET/POST /users)");
        } catch (Exception e) {
            throw new RuntimeException("Failed to start API server", e);
        }
    }
}


