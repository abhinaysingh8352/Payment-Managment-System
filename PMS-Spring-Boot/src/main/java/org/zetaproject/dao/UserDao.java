package org.zetaproject.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.zetaproject.model.entites.User;
import org.zetaproject.model.enums.UserRole;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

@Repository
public class UserDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public User create(User user) {
        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?) RETURNING id";
        Long userId = jdbcTemplate.queryForObject(sql, Long.class, user.getName(), user.getEmail(), user.getPassword(), user.getRole().name());
        user.setId(userId);
        return user;
    }

    public List<User> findAll() {
        String sql = "SELECT id, name, email, role FROM users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            user.setRole(UserRole.valueOf(rs.getString("role")));
            return user;
        });
    }

    public User findByEmail(String email) {
        String sql = "SELECT id, name, email, password, role FROM users WHERE email = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{email}, (rs, rowNum) -> {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRole(UserRole.valueOf(rs.getString("role")));
                return user;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}