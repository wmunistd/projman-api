package dev.wagnermoreira.projman.dao;

import dev.wagnermoreira.projman.db.Database;
import dev.wagnermoreira.projman.domain.Project;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcProjectDao implements ProjectDao {
    @Override
    public long insert(Project project) {
        String sql = "INSERT INTO projects(name, description, start_date, end_date, status, manager_user_id) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, project.getName());
            ps.setString(2, project.getDescription());
            ps.setString(3, project.getStartDate());
            ps.setString(4, project.getEndDate());
            ps.setString(5, project.getStatus());
            if (project.getManagerUserId() == null) ps.setNull(6, Types.INTEGER); else ps.setLong(6, project.getManagerUserId());
            ps.executeUpdate();
            long id;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) return -1;
                id = keys.getLong(1);
            }
            project.setId(id);

            if (project.getTeamIds() != null && !project.getTeamIds().isEmpty()) {
                String link = "INSERT OR IGNORE INTO project_teams(project_id, team_id) VALUES(?, ?)";
                try (PreparedStatement pl = conn.prepareStatement(link)) {
                    for (Long tid : project.getTeamIds()) {
                        pl.setLong(1, id);
                        pl.setLong(2, tid);
                        pl.addBatch();
                    }
                    pl.executeBatch();
                }
            }
            return id;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert project", e);
        }
    }

    @Override
    public List<Project> listAll() {
        String sql = "SELECT id, name, description, start_date, end_date, status, manager_user_id FROM projects ORDER BY name";
        List<Project> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                long id = rs.getLong("id");
                list.add(new Project(
                        id,
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("start_date"),
                        rs.getString("end_date"),
                        rs.getString("status"),
                        rs.getObject("manager_user_id") == null ? null : rs.getLong("manager_user_id"),
                        fetchTeamIds(conn, id)
                ));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list projects", e);
        }
    }

    private static List<Long> fetchTeamIds(Connection conn, long projectId) throws SQLException {
        String sql = "SELECT team_id FROM project_teams WHERE project_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Long> ids = new ArrayList<>();
                while (rs.next()) ids.add(rs.getLong(1));
                return ids;
            }
        }
    }
}
