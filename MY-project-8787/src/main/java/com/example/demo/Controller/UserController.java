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

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.modal.dto.ReservationDto;
import com.example.demo.modal.dto.User;
import com.example.demo.modal.dto.UserRankingDto;
import com.example.demo.service.UserService;

import io.springboot.captcha.utils.CaptchaUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class UserController {
	
	@Autowired
	private UserService userService;
	private static final String ADMIN_EMAIL = "admin@email.com";
	private static final String ADMIN_PASSWORD = "Password123"; 

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
    @Autowired
    private PasswordEncoder passwordEncoder;
   

    
    @GetMapping("/user-name")
    public String getUserName(@RequestParam String email) {
        return userService.getUserNameByEmail(email);
    }
      
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        // Check if the email is already registered
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        int emailCount = jdbcTemplate.queryForObject(sql, new Object[]{user.getEmail()}, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt(1);
            }
        });

        if (emailCount > 0) {
            response.put("success", false);
            response.put("message", "The email is already registered");
            return ResponseEntity.ok(response);
        }

        // Check if the phone number is already registered
        sql = "SELECT COUNT(*) FROM users WHERE phone = ?";
        int phoneCount = jdbcTemplate.queryForObject(sql, new Object[]{user.getPhone()}, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt(1);
            }
        });

        if (phoneCount > 0) {
            response.put("success", false);
            response.put("message", "The phone number is already registered");
            return ResponseEntity.ok(response);
        }

        // Encrypt the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);

        response.put("success", true);
        response.put("message", "Registration successful");
        return ResponseEntity.ok(response);
    }

	  @PostMapping("/login")
	    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
	        String email = loginRequest.get("email");
	        String password = loginRequest.get("password");

	        Map<String, Object> response = new HashMap<>();

	        // Find the user by email
	        User user = userService.getUserByEmail(email);

	        if (user != null) {
	            // Print the incoming credentials and stored credentials for debugging
	            System.out.println("Incoming email: " + email);
	            System.out.println("Incoming password: " + password);
	            System.out.println("Stored password: " + user.getPassword());

	            boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());

	            // Print password match result for debugging
	            System.out.println("Password matches: " + passwordMatches);

	            // Verify the password
	            if (passwordMatches) {
	                response.put("success", true);
	                response.put("message", "Login successful");
	                return ResponseEntity.ok(response);
	            }
	        }

	        response.put("success", false);
	        response.put("message", "Invalid email or password");
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
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
	public ResponseEntity<Map<String, Object>> updatePassword(@PathVariable String email,
			@RequestBody Map<String, String> passwordRequest) {
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

	// for userPreview
	
    @GetMapping("/users/all")
    public ResponseEntity<List<User>> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/users/{email}")
    public ResponseEntity<String> deleteUserByEmail(@PathVariable String email) {
        return userService.deleteUserByEmail(email);
    }
    

 // gender analysis

    @GetMapping("/gender-data")
    @ResponseBody
    public Map<String, Integer> getUserGenderData() {
        return userService.getUserGenderData();
    }

    // age analysis
    
    @GetMapping("/age-data")
    @ResponseBody
    public Map<String, Integer> getUserAgeData() {
        return userService.getUserAgeData();
    }
    
    //password update
    @PutMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, String> passwordRequest) {
        String email = passwordRequest.get("email");
        String newPassword = passwordRequest.get("password");
        Map<String, Object> response = new HashMap<>();

        // 確認用戶存在
        User user = userService.getUserByEmail(email);
        if (user == null) {
            response.put("success", false);
            response.put("message", "用戶不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // 更新密碼
        if (userService.updatePassword(email, passwordEncoder.encode(newPassword))) {
            response.put("success", true);
            response.put("message", "密碼更新成功");
        } else {
            response.put("success", false);
            response.put("message", "密碼更新失敗");
        }
        return ResponseEntity.ok(response);
    }
    
    
}