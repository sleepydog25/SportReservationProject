package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;

import com.example.demo.modal.dto.ReservationDto;
import com.example.demo.modal.dto.User;
import com.example.demo.modal.dto.UserRankingDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	
	 public String getUserNameByEmail(String email) {
	        String sql = "SELECT name FROM users WHERE email = ?";
	        try {
	            return jdbcTemplate.queryForObject(sql, new Object[]{email}, String.class);
	        } catch (EmptyResultDataAccessException e) {
	            // 如果沒有找到記錄，返回 null
	            return null;
	        }
	    }

	public User getUserByEmail(String email) {
		String sql = "SELECT * FROM users WHERE email = ?";
		try {
			return jdbcTemplate.queryForObject(sql, new Object[] { email }, new RowMapper<User>() {
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
	
	public void save (User user) {
		String sql = "INSERT INTO users (uuid, name, gender, birthday, phone, email, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
		jdbcTemplate.update(sql, user.getUuid(), user.getName(), user.getGender(), user.getBirthday(), user.getPhone(),
				user.getEmail(), user.getPassword());
	}

	public User updateUserByEmail(String email, User updatedUser) {
		String sql = "UPDATE users SET name = ?, gender = ?, birthday = ?, phone = ?, email = ? WHERE email = ?";
		int rowsAffected = jdbcTemplate.update(sql, updatedUser.getName(), updatedUser.getGender(),
				updatedUser.getBirthday(), updatedUser.getPhone(), updatedUser.getEmail(), email);

		if (rowsAffected > 0) {
			return getUserByEmail(updatedUser.getEmail());
		} else {
			return null;
		}
	}
	// userPreview

	// 獲取所有使用者的邏輯
	public ResponseEntity<List<User>> getAllUsers() {
	    String sql = "SELECT id, name, gender, birthday, email, phone FROM users";
	    List<User> users = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(User.class));
	    return ResponseEntity.ok(users);
	}

	// 刪除指定 email 使用者的邏輯
	public ResponseEntity<String> deleteUserByEmail(String email) {
	    String sql = "DELETE FROM users WHERE email = ?";
	    int rowsAffected = jdbcTemplate.update(sql, email);
	    if (rowsAffected > 0) {
	        return ResponseEntity.ok("User with email " + email + " deleted successfully");
	    } else {
	        return ResponseEntity.ok("User with email " + email + " not found");
	    }
	}
	
	//user gender analysis
    public Map<String, Integer> getUserGenderData() {
        String sql = "SELECT gender, COUNT(*) AS count FROM users GROUP BY gender";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        int totalUsers = 0;
        int maleCount = 0;
        int femaleCount = 0;
        int othersCount =0;

        for (Map<String, Object> row : rows) {
            String gender = (String) row.get("gender");
            int count = ((Number) row.get("count")).intValue();
            totalUsers += count;

            if (gender.equalsIgnoreCase("male")) {
                maleCount = count;
            } else if (gender.equalsIgnoreCase("female")) {
                femaleCount = count;
            }else {
            	othersCount =count;
            }
        }

        Map<String, Integer> genderData = new HashMap<>();
        genderData.put("男性", (maleCount * 100) / totalUsers);
        genderData.put("女性", (femaleCount * 100) / totalUsers);
        genderData.put("其他", (othersCount * 100) / totalUsers);

        return genderData;
    }
    
    public Map<String, Integer> getUserAgeData() {
        String sql = "SELECT birthday FROM users";
        List<String> birthdays = jdbcTemplate.queryForList(sql, String.class);

        int under18 = 0;
        int from18To25 = 0;
        int from26To35 = 0;
        int from36To45 = 0;
        int from46To55 = 0;
        int from56To65 = 0;
        int over65 = 0;

        for (String birthday : birthdays) {
            int age = calculateAge(birthday);
            if (age < 18) {
                under18++;
            } else if (age <= 25) {
                from18To25++;
            } else if (age <= 35) {
                from26To35++;
            } else if (age <= 45) {
                from36To45++;
            } else if (age <= 55) {
                from46To55++;
            } else if (age <= 65) {
                from56To65++;
            } else {
                over65++;
            }
        }

        Map<String, Integer> ageData = new HashMap<>();
        ageData.put("18歲以下", under18);
        ageData.put("18到25歲", from18To25);
        ageData.put("26歲~35歲", from26To35);
        ageData.put("36歲~45歲", from36To45);
        ageData.put("46歲~55歲", from46To55);
        ageData.put("56歲~65歲", from56To65);
        ageData.put("65歲以上", over65);

        return ageData;
    }

    private int calculateAge(String birthday) {
        LocalDate birthDate = LocalDate.parse(birthday);
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDate, currentDate).getYears();
    }
    
    public boolean updatePassword(String email, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE email = ?";
        int rows = jdbcTemplate.update(sql, newPassword, email);
        return rows > 0;
    }

}
