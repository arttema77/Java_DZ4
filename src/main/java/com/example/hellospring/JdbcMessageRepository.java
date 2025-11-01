package com.example.hellospring;

import java.sql.ResultSet;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class JdbcMessageRepository {
    private final JdbcTemplate jdbc;

    private final RowMapper<Message> mapper = (rs, rn) -> {
        Message m = new Message();
        m.setId(rs.getLong("id"));
        m.setContent(rs.getString("content"));
        m.setAuthorId(rs.getLong("author_id"));
        m.setAuthorUsername(rs.getString("username"));
        m.setAuthorDisplayName(rs.getString("display_name"));
        m.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return m;
    };

    public JdbcMessageRepository(JdbcTemplate jdbc){ this.jdbc = jdbc; }

    public Message save(Message m){
        var kh = new GeneratedKeyHolder();

        jdbc.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO messages (content, author_id) VALUES (?, ?)",
                    new String[] {"id"}               // <-- здесь тоже
            );
            ps.setString(1, m.getContent());
            ps.setLong(2, m.getAuthorId());
            return ps;
        }, kh);

        if (kh.getKey() != null) {
            m.setId(kh.getKey().longValue());
        }
        return m;
    }

    public Optional<Message> findById(Long id){
        var sql = """
            SELECT m.id, m.content, m.author_id, m.created_at,
                   u.username, u.display_name
            FROM messages m
            JOIN users u ON u.id = m.author_id
            WHERE m.id = ?
            """;
        var list = jdbc.query(sql, mapper, id);
        return list.isEmpty()? Optional.empty(): Optional.of(list.get(0));
    }

    public List<Message> findPage(int page, int size){
        int offset = Math.max(0,page) * Math.max(1,size);
        var sql = """
            SELECT m.id, m.content, m.author_id, m.created_at,
                   u.username, u.display_name
            FROM messages m
            JOIN users u ON u.id = m.author_id
            ORDER BY m.created_at DESC
            LIMIT ? OFFSET ?
            """;
        return jdbc.query(sql, mapper, size, offset);
    }

    public List<Message> findByAuthorUsername(String username, int page, int size){
        int offset = Math.max(0,page) * Math.max(1,size);
        var sql = """
            SELECT m.id, m.content, m.author_id, m.created_at,
                   u.username, u.display_name
            FROM messages m
            JOIN users u ON u.id = m.author_id
            WHERE u.username = ?
            ORDER BY m.created_at DESC
            LIMIT ? OFFSET ?
            """;
        return jdbc.query(sql, mapper, username, size, offset);
    }

    public int[] saveBatch(List<Message> list) {
        final String sql = "INSERT INTO messages (content, author_id) VALUES (?, ?)";

        return jdbc.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Message m = list.get(i);
                ps.setString(1, m.getContent());
                ps.setLong(2, m.getAuthorId());
            }

            @Override
            public int getBatchSize() {
                return list.size();
            }
        });
    }



    public void clearAll(){
        jdbc.update("DELETE FROM messages");
        jdbc.update("DELETE FROM users");
    }
}
