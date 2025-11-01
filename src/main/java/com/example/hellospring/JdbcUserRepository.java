package com.example.hellospring;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcUserRepository {
    private final JdbcTemplate jdbc;
    private final RowMapper<User> mapper = (rs, rn) -> {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setUsername(rs.getString("username"));
        u.setDisplayName(rs.getString("display_name"));
        u.setEmail(rs.getString("email"));
        return u;
    };

    public JdbcUserRepository(JdbcTemplate jdbc){ this.jdbc = jdbc; }

    public User save(User u){
        var kh = new GeneratedKeyHolder();

        jdbc.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users (username, display_name, email) VALUES (?, ?, ?)",
                    new String[] {"id"}               // <-- ключевая строка
            );
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getDisplayName());
            ps.setString(3, u.getEmail());
            return ps;
        }, kh);

        if (kh.getKey() != null) {
            u.setId(kh.getKey().longValue());
        }
        return u;
    }

    public Optional<User> findByUsername(String username){
        var list = jdbc.query("SELECT * FROM users WHERE username=?", mapper, username);
        return list.isEmpty()? Optional.empty(): Optional.of(list.get(0));
    }
}
