package dev.wagnermoreira.projman.dao;

import dev.wagnermoreira.projman.db.Database;
import dev.wagnermoreira.projman.domain.User;
import dev.wagnermoreira.projman.domain.UserRole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcUserDao implements UserDao {
    @Override
    public long insert(User user) {
        String sql = "INSERT INTO users(full_name, cpf, email, job_title, username, password_hash, role) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getCpf());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getJobTitle());
            ps.setString(5, user.getUsername());
            ps.setString(6, user.getPasswordHash());
            ps.setString(7, user.getRole().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    user.setId(id);
                    return id;
                }
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert user", e);
        }
    }

    @Override
    public List<User> listAll() {
        String sql = "SELECT id, full_name, cpf, email, job_title, username, password_hash, role FROM users ORDER BY full_name";
        List<User> users = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list users", e);
        }
        return users;
    }

    private static User mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String fullName = rs.getString("full_name");
        String cpf = rs.getString("cpf");
        String email = rs.getString("email");
        String jobTitle = rs.getString("job_title");
        String username = rs.getString("username");
        String passwordHash = rs.getString("password_hash");
        UserRole role = UserRole.valueOf(rs.getString("role"));
        return new User(id, fullName, cpf, email, jobTitle, username, passwordHash, role);
    }
}


