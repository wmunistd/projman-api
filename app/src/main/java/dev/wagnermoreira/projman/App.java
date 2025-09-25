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
            int port = 8080;
            String envPort = System.getenv("PROJMAN_PORT");
            if (envPort != null && !envPort.isBlank()) {
                try { port = Integer.parseInt(envPort.trim()); } catch (NumberFormatException ignored) {}
            }
            ApiServer server = new ApiServer(port, userDao);
            server.start();
            System.out.println("API running on http://localhost:" + port + " (GET/POST /users)");
        } catch (Exception e) {
            throw new RuntimeException("Failed to start API server", e);
        }
    }
}


