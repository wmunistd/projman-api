package dev.wagnermoreira.projman.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String JDBC_URL = "jdbc:sqlite:" + resolveDbPath();

    private static String resolveDbPath() {
        String env = System.getenv("PROJMAN_DB");
        if (env == null || env.isBlank()) {
            return "projman.db";
        }
        return env;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL);
    }

    public static void initSchema() {
        String createUsers = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "full_name TEXT NOT NULL, " +
                "cpf TEXT NOT NULL UNIQUE, " +
                "email TEXT NOT NULL UNIQUE, " +
                "job_title TEXT NOT NULL, " +
                "username TEXT NOT NULL UNIQUE, " +
                "password_hash TEXT NOT NULL, " +
                "role TEXT NOT NULL" +
                ")";

        String createTeams = "CREATE TABLE IF NOT EXISTS teams (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL UNIQUE, " +
                "description TEXT" +
                ")";

        String createTeamMembers = "CREATE TABLE IF NOT EXISTS team_members (" +
                "team_id INTEGER NOT NULL, " +
                "user_id INTEGER NOT NULL, " +
                "PRIMARY KEY (team_id, user_id), " +
                "FOREIGN KEY(team_id) REFERENCES teams(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE" +
                ")";

        String createProjects = "CREATE TABLE IF NOT EXISTS projects (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL UNIQUE, " +
                "description TEXT, " +
                "start_date TEXT, " +
                "end_date TEXT, " +
                "status TEXT NOT NULL, " +
                "manager_user_id INTEGER" +
                ")";

        String createProjectTeams = "CREATE TABLE IF NOT EXISTS project_teams (" +
                "project_id INTEGER NOT NULL, " +
                "team_id INTEGER NOT NULL, " +
                "PRIMARY KEY (project_id, team_id), " +
                "FOREIGN KEY(project_id) REFERENCES projects(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(team_id) REFERENCES teams(id) ON DELETE CASCADE" +
                ")";

        String createTasks = "CREATE TABLE IF NOT EXISTS tasks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "status TEXT NOT NULL, " +
                "project_id INTEGER, " +
                "assignee_id INTEGER, " +
                "FOREIGN KEY(project_id) REFERENCES projects(id) ON DELETE SET NULL, " +
                "FOREIGN KEY(assignee_id) REFERENCES users(id) ON DELETE SET NULL" +
                ")";

        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            st.execute(createUsers);
            st.execute(createTeams);
            st.execute(createTeamMembers);
            st.execute(createProjects);
            st.execute(createProjectTeams);
            st.execute(createTasks);
            // Tenta adicionar a coluna manager_user_id se o banco de dados não tiver
            try (Statement st2 = conn.createStatement()) {
                st2.execute("ALTER TABLE projects ADD COLUMN manager_user_id INTEGER");
            } catch (SQLException ignored) {
                // Coluna manager_user_id já existe
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database schema", e);
        }
    }
}


