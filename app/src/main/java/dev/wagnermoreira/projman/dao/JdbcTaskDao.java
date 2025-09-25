package dev.wagnermoreira.projman.dao;

import dev.wagnermoreira.projman.db.Database;
import dev.wagnermoreira.projman.domain.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcTaskDao implements TaskDao {
    @Override
    public long insert(Task task) {
        String sql = "INSERT INTO tasks(title, status, project_id, assignee_id) VALUES(?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getStatus());
            if (task.getProjectId() == null) ps.setNull(3, Types.INTEGER); else ps.setLong(3, task.getProjectId());
            if (task.getAssigneeId() == null) ps.setNull(4, Types.INTEGER); else ps.setLong(4, task.getAssigneeId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    task.setId(id);
                    return id;
                }
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert task", e);
        }
    }

    @Override
    public List<Task> listAll() {
        return query("SELECT id, title, status, project_id, assignee_id FROM tasks ORDER BY id", null);
    }

    @Override
    public List<Task> listByProject(Long projectId) {
        return query("SELECT id, title, status, project_id, assignee_id FROM tasks WHERE project_id=? ORDER BY id", projectId);
    }

    @Override
    public boolean updateStatus(long id, String status) {
        String sql = "UPDATE tasks SET status=? WHERE id=?";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update task status", e);
        }
    }

    private static List<Task> query(String sql, Long projectId) {
        List<Task> list = new ArrayList<>();
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            if (projectId != null) ps.setLong(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Task(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("status"),
                            rs.getObject("project_id") == null ? null : rs.getLong("project_id"),
                            rs.getObject("assignee_id") == null ? null : rs.getLong("assignee_id")
                    ));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query tasks", e);
        }
    }
}
