//package com.example.demo.Controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.example.demo.modal.dto.UserDto;
//import com.example.demo.modal.dto.LoginRequest;
//import com.example.demo.modal.dto.LoginResponse;
//import com.example.demo.service.UserService;
//
//import jakarta.servlet.http.HttpSession;
//
//@RestController
//@RequestMapping("/api/auth")
//public class UserController {
//
//    @Autowired
//    private UserService userService;
//
//    /**
//     * Handles user login requests.
//     * @param loginRequest the login request containing email and password
//     * @param session the current HTTP session
//     * @return a ResponseEntity containing the login response
//     */
//    @PostMapping("/login")
//    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
//        UserDto user = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
//        if (user != null) {
//            // Save user session information
//            session.setAttribute("user", user);
//            return ResponseEntity.ok(new LoginResponse(true, "Login successful"));
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse(false, "Invalid email or password"));
//        }
//    }
//}


//Successful login
//package com.example.demo.Controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.example.demo.modal.dto.UserDto;
//import com.example.demo.modal.dto.LoginRequest;
//import com.example.demo.modal.dto.LoginResponse;
//import com.example.demo.service.UserService;
//
//import jakarta.servlet.http.HttpSession;
//
//@RestController
//@RequestMapping("/api/auth")
//public class UserController {
//
//    @Autowired
//    private UserService userService;
//
//    /**
//     * Handles user login requests.
//     * @param loginRequest the login request containing email and password
//     * @param session the current HTTP session
//     * @return a ResponseEntity containing the login response
//     */
//    @PostMapping("/login")
//    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
//        UserDto user = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
//        if (user != null) {
//            // Save user session information
//            session.setAttribute("user", user);
//            return ResponseEntity.ok(new LoginResponse(true, "Login successful"));
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse(false, "Invalid email or password"));
//        }
//    }
//}

package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.modal.dto.User;
import com.example.demo.modal.dto.UserRankingDto;
import com.example.demo.service.UserService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        // 檢查是否有重複的email
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        int emailCount = jdbcTemplate.queryForObject(sql, new Object[]{user.getEmail()}, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt(1);
            }
        });

        if (emailCount > 0) {
            response.put("success", false);
            response.put("message", "該電子信箱已被註冊");
            return ResponseEntity.ok(response);
        }

        // 檢查是否有重複的手機號碼
        sql = "SELECT COUNT(*) FROM users WHERE phone = ?";
        int phoneCount = jdbcTemplate.queryForObject(sql, new Object[]{user.getPhone()}, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt(1);
            }
        });

        if (phoneCount > 0) {
            response.put("success", false);
            response.put("message", "該行動電話已被註冊");
            return ResponseEntity.ok(response);
        }

        // 插入用戶數據，包括UUID
        sql = "INSERT INTO users (uuid, name, gender, birthday, phone, email, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getUuid(), user.getName(), user.getGender(), user.getBirthday(), user.getPhone(), user.getEmail(), user.getPassword());

        response.put("success", true);
        response.put("message", "註冊成功");
        return ResponseEntity.ok(response);
    }
    
//    @PostMapping("/login")
//    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
//        String email = loginRequest.get("email");
//        String password = loginRequest.get("password");
//
//        Map<String, Object> response = new HashMap<>();
//
//        // 檢查是否是管理員
//        if (ADMIN_EMAIL.equals(email) && ADMIN_PASSWORD.equals(password)) {
//            response.put("success", true);
//            response.put("admin", true);
//            return ResponseEntity.ok(response);
//        }
//
//        // 查找用戶
//        String sql = "SELECT * FROM users WHERE email = ?";
//        User user = jdbcTemplate.queryForObject(sql, new Object[]{email}, new RowMapper<User>() {
//            @Override
//            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
//                User user = new User();
//                user.setId(rs.getInt("id"));
//                user.setUuid(rs.getString("uuid"));
//                user.setName(rs.getString("name"));
//                user.setGender(rs.getString("gender"));
//                user.setBirthday(rs.getString("birthday"));
//                user.setPhone(rs.getString("phone"));
//                user.setEmail(rs.getString("email"));
//                user.setPassword(rs.getString("password"));
//                return user;
//            }
//        });
//
//        // 驗證密碼
//        if (user != null && user.getPassword().equals(password)) {
//            response.put("success", true);
//            response.put("message", "登入成功");
//            return ResponseEntity.ok(response);
//        } else {
//            response.put("success", false);
//            response.put("message", "電子郵件或密碼錯誤");
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
//        }
//    }
    
    
    @Autowired
    private UserService userService;
    private static final String ADMIN_EMAIL = "admin@email.com";
    private static final String ADMIN_PASSWORD = "Admin123456789";
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        Map<String, Object> response = new HashMap<>();

        // 檢查是否是管理員
        if (ADMIN_EMAIL.equals(email) && ADMIN_PASSWORD.equals(password)) {
            response.put("success", true);
            response.put("admin", true);
            return ResponseEntity.ok(response);
        }

        // 查找用戶
        User user = userService.getUserByEmail(email);

        // 驗證密碼
        if (user != null && user.getPassword().equals(password)) {
            response.put("success", true);
            response.put("admin", false);
            response.put("message", "登入成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "電子郵件或密碼錯誤");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    

    @GetMapping("/users/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/users/{email}")
    public ResponseEntity<User> updateUserByEmail(@PathVariable String email, @RequestBody User updatedUser) {
        // 不更新密碼
        User user = userService.updateUserByEmail(email, updatedUser);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/users/{email}/password")
    public ResponseEntity<Map<String, Object>> updatePassword(@PathVariable String email, @RequestBody Map<String, String> passwordRequest) {
        String newPassword = passwordRequest.get("password");
        Map<String, Object> response = new HashMap<>();

        if (userService.updatePassword(email, newPassword)) {
            response.put("success", true);
            response.put("message", "密碼更新成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "密碼更新失敗");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    

}