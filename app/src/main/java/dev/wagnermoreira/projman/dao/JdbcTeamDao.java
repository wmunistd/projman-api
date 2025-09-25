package dev.wagnermoreira.projman.dao;

import dev.wagnermoreira.projman.db.Database;
import dev.wagnermoreira.projman.domain.Team;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcTeamDao implements TeamDao {
    @Override
    public long insert(Team team) {
        String sqlTeam = "INSERT INTO teams(name, description) VALUES(?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlTeam, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, team.getName());
            ps.setString(2, team.getDescription());
            ps.executeUpdate();
            long teamId;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) return -1;
                teamId = keys.getLong(1);
            }
            team.setId(teamId);

            if (team.getMemberIds() != null && !team.getMemberIds().isEmpty()) {
                String sqlMember = "INSERT OR IGNORE INTO team_members(team_id, user_id) VALUES(?, ?)";
                try (PreparedStatement pm = conn.prepareStatement(sqlMember)) {
                    for (Long uid : team.getMemberIds()) {
                        pm.setLong(1, teamId);
                        pm.setLong(2, uid);
                        pm.addBatch();
                    }
                    pm.executeBatch();
                }
            }
            return teamId;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert team", e);
        }
    }

    @Override
    public List<Team> listAll() {
        String sql = "SELECT t.id, t.name, t.description FROM teams t ORDER BY t.name";
        List<Team> teams = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                long id = rs.getLong("id");
                teams.add(new Team(id, rs.getString("name"), rs.getString("description"), fetchMemberIds(conn, id)));
            }
            return teams;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list teams", e);
        }
    }

    private static List<Long> fetchMemberIds(Connection conn, long teamId) throws SQLException {
        String sql = "SELECT user_id FROM team_members WHERE team_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, teamId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Long> ids = new ArrayList<>();
                while (rs.next()) ids.add(rs.getLong(1));
                return ids;
            }
        }
    }
}
