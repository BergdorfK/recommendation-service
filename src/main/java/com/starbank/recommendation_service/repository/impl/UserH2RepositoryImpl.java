package com.starbank.recommendation_service.repository.impl;

import com.starbank.recommendation_service.model.UserH2;
import com.starbank.recommendation_service.repository.UserH2Repository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class UserH2RepositoryImpl implements UserH2Repository {

    private final JdbcTemplate jdbc;

    public UserH2RepositoryImpl(@Qualifier("h2JdbcTemplate") JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static class UserRowMapper implements RowMapper<UserH2> {
        @Override
        public UserH2 mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserH2 u = new UserH2();
            u.setId(rs.getLong("id"));
            u.setUserId(rs.getString("user_id"));
            u.setUsername(rs.getString("username"));
            u.setFirstName(rs.getString("first_name"));
            u.setLastName(rs.getString("last_name"));
            return u;
        }
    }

    @Override
    public Optional<UserH2> findByUsername(String username) {
        String sql = """
            SELECT id, user_id, username, first_name, last_name
            FROM users
            WHERE username = ?
            """;
        return jdbc.query(sql, new UserRowMapper(), username).stream().findFirst();
    }

    @Override
    public Optional<UserH2> findByUserId(String userId) {
        String sql = """
            SELECT id, user_id, username, first_name, last_name
            FROM users
            WHERE user_id = ?
            """;
        return jdbc.query(sql, new UserRowMapper(), userId).stream().findFirst();
    }
}