package com.example.demo.Controller;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    @PostMapping("/api/authlogin")
    public Map<String, Object> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        Map<String, Object> response = new HashMap<>();

        // 檢查特定的電子郵件和密碼組合
        if ("admin@example.com".equals(email) && "Admin123456".equals(password)) {
            response.put("success", true);
            response.put("special", true);
        } else {
            response.put("success", false);
            response.put("message", "Invalid email or password");
        }

        return response;
    }
}
