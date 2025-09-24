package dev.wagnermoreira.projman.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dev.wagnermoreira.projman.api.dto.UserCreateRequest;
import dev.wagnermoreira.projman.api.dto.UserResponse;
import dev.wagnermoreira.projman.dao.UserDao;
import dev.wagnermoreira.projman.domain.User;
import dev.wagnermoreira.projman.domain.UserRole;
import dev.wagnermoreira.projman.security.PasswordHasher;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class ApiServer {
    private final HttpServer server;
    private final Gson gson;
    private final UserDao userDao;

    public ApiServer(int port, UserDao userDao) throws IOException {
        this.userDao = userDao;
        this.gson = new GsonBuilder().create();
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/users", new UsersHandler());
        server.createContext("/health", exchange -> {
            applyCors(exchange);
            if ("GET".equals(exchange.getRequestMethod())) {
                respondJson(exchange, 200, "{\"status\":\"ok\"}");
            } else {
                respondJson(exchange, 405, "{}\n");
            }
        });
    }

    public void start() {
        server.start();
    }

    private class UsersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            applyCors(exchange);
            switch (exchange.getRequestMethod()) {
                case "OPTIONS":
                    respondJson(exchange, 204, "");
                    break;
                case "GET":
                    handleList(exchange);
                    break;
                case "POST":
                    handleCreate(exchange);
                    break;
                default:
                    respondJson(exchange, 405, "{}\n");
            }
        }

        private void handleList(HttpExchange exchange) throws IOException {
            List<User> users = userDao.listAll();
            List<UserResponse> out = users.stream().map(u -> {
                UserResponse r = new UserResponse();
                r.id = u.getId();
                r.fullName = u.getFullName();
                r.email = u.getEmail();
                r.jobTitle = u.getJobTitle();
                r.username = u.getUsername();
                r.role = u.getRole().name();
                return r;
            }).collect(Collectors.toList());
            respondJson(exchange, 200, gson.toJson(out));
        }

        private void handleCreate(HttpExchange exchange) throws IOException {
            UserCreateRequest in = gson.fromJson(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8), UserCreateRequest.class);
            if (in == null || in.fullName == null || in.email == null || in.username == null || in.password == null || in.role == null) {
                respondJson(exchange, 400, "{\"error\":\"missing fields\"}\n");
                return;
            }
            String hash = PasswordHasher.hash(in.password);
            User user = User.ofNew(in.fullName, in.cpf, in.email, in.jobTitle, in.username, hash, UserRole.valueOf(in.role));
            long id = userDao.insert(user);

            UserResponse r = new UserResponse();
            r.id = id;
            r.fullName = user.getFullName();
            r.email = user.getEmail();
            r.jobTitle = user.getJobTitle();
            r.username = user.getUsername();
            r.role = user.getRole().name();
            respondJson(exchange, 201, gson.toJson(r));
        }
    }

    private static void applyCors(HttpExchange exchange) {
        Headers h = exchange.getResponseHeaders();
        h.add("Access-Control-Allow-Origin", "http://localhost:5173");
        h.add("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
        h.add("Access-Control-Allow-Headers", "Content-Type");
    }

    private static void respondJson(HttpExchange exchange, int status, String body) throws IOException {
        Headers h = exchange.getResponseHeaders();
        h.add("Content-Type", "application/json; charset=utf-8");
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
