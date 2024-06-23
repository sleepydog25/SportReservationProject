package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.dao.EmptyResultDataAccessException;

import com.example.demo.modal.dto.User;
import com.example.demo.modal.dto.UserRankingDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{email}, new RowMapper<User>() {
                @Override
                public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUuid(rs.getString("uuid"));
                    user.setName(rs.getString("name"));
                    user.setGender(rs.getString("gender"));
                    user.setBirthday(rs.getString("birthday"));
                    user.setPhone(rs.getString("phone"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    return user;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            // 如果沒有找到記錄，返回 null
            return null;
        }
    }

    public User updateUserByEmail(String email, User updatedUser) {
        String sql = "UPDATE users SET name = ?, gender = ?, birthday = ?, phone = ?, email = ? WHERE email = ?";
        int rowsAffected = jdbcTemplate.update(sql, updatedUser.getName(), updatedUser.getGender(), updatedUser.getBirthday(), updatedUser.getPhone(), updatedUser.getEmail(), email);

        if (rowsAffected > 0) {
            return getUserByEmail(updatedUser.getEmail());
        } else {
            return null;
        }
    }

    public boolean updatePassword(String email, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE email = ?";
        int rowsAffected = jdbcTemplate.update(sql, newPassword, email);

        return rowsAffected > 0;
    }
    

}