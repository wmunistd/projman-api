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
import dev.wagnermoreira.projman.dao.TeamDao;
import dev.wagnermoreira.projman.dao.JdbcTeamDao;
import dev.wagnermoreira.projman.api.dto.TeamDtos.TeamCreateRequest;
import dev.wagnermoreira.projman.api.dto.TeamDtos.TeamResponse;
import dev.wagnermoreira.projman.domain.Team;
import dev.wagnermoreira.projman.domain.User;
import dev.wagnermoreira.projman.domain.UserRole;
import dev.wagnermoreira.projman.security.PasswordHasher;
import dev.wagnermoreira.projman.dao.ProjectDao;
import dev.wagnermoreira.projman.dao.JdbcProjectDao;
import dev.wagnermoreira.projman.api.dto.ProjectDtos.ProjectCreateRequest;
import dev.wagnermoreira.projman.api.dto.ProjectDtos.ProjectResponse;
import dev.wagnermoreira.projman.domain.Project;
import dev.wagnermoreira.projman.dao.TaskDao;
import dev.wagnermoreira.projman.dao.JdbcTaskDao;
import dev.wagnermoreira.projman.api.dto.TaskDtos.TaskCreateRequest;
import dev.wagnermoreira.projman.api.dto.TaskDtos.TaskResponse;
import dev.wagnermoreira.projman.api.dto.TaskDtos.TaskStatusUpdate;
import dev.wagnermoreira.projman.domain.Task;

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
    private final TeamDao teamDao;
    private final ProjectDao projectDao;
    private final TaskDao taskDao;

    public ApiServer(int port, UserDao userDao) throws IOException {
        this.userDao = userDao;
        this.teamDao = new JdbcTeamDao();
        this.projectDao = new JdbcProjectDao();
        this.gson = new GsonBuilder().create();
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/users", new UsersHandler());
        server.createContext("/teams", new TeamsHandler());
        server.createContext("/projects", new ProjectsHandler());
        this.taskDao = new JdbcTaskDao();
        server.createContext("/tasks", new TasksHandler());
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
            try {
                UserCreateRequest in = gson.fromJson(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8), UserCreateRequest.class);
                if (in == null || in.fullName == null || in.fullName.isBlank() ||
                        in.cpf == null || in.cpf.isBlank() ||
                        in.email == null || in.email.isBlank() ||
                        in.jobTitle == null || in.jobTitle.isBlank() ||
                        in.username == null || in.username.isBlank() ||
                        in.password == null || in.password.isBlank() ||
                        in.role == null || in.role.isBlank()) {
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
            } catch (Exception e) {
                Throwable root = e;
                while (root.getCause() != null) {
                    root = root.getCause();
                }
                String msg = root.getMessage();
                if (msg == null || msg.isBlank()) {
                    msg = "failed";
                }
                msg = msg.replace("\\", "\\\\").replace("\"", "\\\"");
                int status = msg.toLowerCase().contains("unique constraint failed") ? 409 : 400;
                respondJson(exchange, status, "{\"error\":\"" + msg + "\"}\n");
            }
        }
    }

    private class TeamsHandler implements HttpHandler {
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
            List<Team> teams = teamDao.listAll();
            List<TeamResponse> out = teams.stream().map(t -> {
                TeamResponse r = new TeamResponse();
                r.id = t.getId();
                r.name = t.getName();
                r.description = t.getDescription();
                r.memberIds = t.getMemberIds();
                return r;
            }).collect(Collectors.toList());
            respondJson(exchange, 200, gson.toJson(out));
        }

        private void handleCreate(HttpExchange exchange) throws IOException {
            TeamCreateRequest in = gson.fromJson(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8), TeamCreateRequest.class);
            if (in == null || in.name == null || in.name.isBlank()) {
                respondJson(exchange, 400, "{\"error\":\"name required\"}\n");
                return;
            }
            Team team = Team.ofNew(in.name, in.description, in.memberIds);
            long id = teamDao.insert(team);
            TeamResponse r = new TeamResponse();
            r.id = id;
            r.name = team.getName();
            r.description = team.getDescription();
            r.memberIds = team.getMemberIds();
            respondJson(exchange, 201, gson.toJson(r));
        }
    }

    private class ProjectsHandler implements HttpHandler {
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
            List<Project> projects = projectDao.listAll();
            List<ProjectResponse> out = projects.stream().map(p -> {
                ProjectResponse r = new ProjectResponse();
                r.id = p.getId();
                r.name = p.getName();
                r.description = p.getDescription();
                r.startDate = p.getStartDate();
                r.endDate = p.getEndDate();
                r.status = p.getStatus();
                r.managerUserId = p.getManagerUserId();
                r.teamIds = p.getTeamIds();
                return r;
            }).collect(Collectors.toList());
            respondJson(exchange, 200, gson.toJson(out));
        }

        private void handleCreate(HttpExchange exchange) throws IOException {
            ProjectCreateRequest in = gson.fromJson(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8), ProjectCreateRequest.class);
            if (in == null || in.name == null || in.name.isBlank() || in.status == null) {
                respondJson(exchange, 400, "{\"error\":\"name and status required\"}\n");
                return;
            }
            Project project = Project.ofNew(in.name, in.description, in.startDate, in.endDate, in.status, in.managerUserId, in.teamIds);
            long id = projectDao.insert(project);
            ProjectResponse r = new ProjectResponse();
            r.id = id;
            r.name = project.getName();
            r.description = project.getDescription();
            r.startDate = project.getStartDate();
            r.endDate = project.getEndDate();
            r.status = project.getStatus();
            r.managerUserId = project.getManagerUserId();
            r.teamIds = project.getTeamIds();
            respondJson(exchange, 201, gson.toJson(r));
        }
    }

    private static void applyCors(HttpExchange exchange) {
        Headers h = exchange.getResponseHeaders();
        // Permite qualquer origem para dev/testing local
        h.add("Access-Control-Allow-Origin", "*");
        h.add("Access-Control-Allow-Methods", "GET,POST,PUT,OPTIONS");
        h.add("Access-Control-Allow-Headers", "Content-Type, Accept");
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

    private class TasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            applyCors(exchange);
            String method = exchange.getRequestMethod();
            if ("OPTIONS".equals(method)) { respondJson(exchange, 204, ""); return; }

            String path = exchange.getRequestURI().getPath();
            if (method.equals("PUT") && path.matches("/tasks/\\d+/status")) {
                handleStatusUpdate(exchange);
                return;
            }
            switch (method) {
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
            String query = exchange.getRequestURI().getQuery();
            List<Task> tasks;
            if (query != null && query.startsWith("projectId=")) {
                Long pid = Long.parseLong(query.substring("projectId=".length()));
                tasks = taskDao.listByProject(pid);
            } else {
                tasks = taskDao.listAll();
            }
            List<TaskResponse> out = tasks.stream().map(t -> {
                TaskResponse r = new TaskResponse();
                r.id = t.getId();
                r.title = t.getTitle();
                r.status = t.getStatus();
                r.projectId = t.getProjectId();
                r.assigneeId = t.getAssigneeId();
                return r;
            }).collect(Collectors.toList());
            respondJson(exchange, 200, gson.toJson(out));
        }

        private void handleCreate(HttpExchange exchange) throws IOException {
            TaskCreateRequest in = gson.fromJson(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8), TaskCreateRequest.class);
            if (in == null || in.title == null || in.title.isBlank()) {
                respondJson(exchange, 400, "{\"error\":\"title required\"}\n");
                return;
            }
            String status = (in.status == null || in.status.isBlank()) ? "PLANNED" : in.status;
            Task task = Task.ofNew(in.title, status, in.projectId, in.assigneeId);
            long id = taskDao.insert(task);
            TaskResponse r = new TaskResponse();
            r.id = id;
            r.title = task.getTitle();
            r.status = task.getStatus();
            r.projectId = task.getProjectId();
            r.assigneeId = task.getAssigneeId();
            respondJson(exchange, 201, gson.toJson(r));
        }

        private void handleStatusUpdate(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String idStr = path.replaceAll("^/tasks/", "").replaceAll("/status$", "");
            long id = Long.parseLong(idStr);
            TaskStatusUpdate in = gson.fromJson(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8), TaskStatusUpdate.class);
            if (in == null || in.status == null || in.status.isBlank()) {
                respondJson(exchange, 400, "{\"error\":\"status required\"}\n");
                return;
            }
            boolean ok = taskDao.updateStatus(id, in.status);
            if (!ok) { respondJson(exchange, 404, "{\"error\":\"not found\"}\n"); return; }
            respondJson(exchange, 200, "{}\n");
        }
    }
}
