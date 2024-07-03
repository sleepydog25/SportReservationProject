package com.example.demo.Controller;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.EmailService;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/forget-password")
public class ForgetPasswordController {

    @Autowired
    private EmailService emailService; // 假設您有一個EmailService來發送郵件

    @Autowired
    private UserService userService; // 假設您有一個UserService來處理使用者的資料庫操作

    @PostMapping
    public Map<String, Object> forgetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Map<String, Object> response = new HashMap<>();
        
        // 生成8個隨機字符的新密碼，包含至少一個大寫字母、一個小寫字母和一個數字
        String newPassword = generateRandomPassword();
        
        // 更新資料庫中的使用者密碼
        boolean updateSuccess = userService.updatePassword(email, newPassword);

        if (updateSuccess) {
            // 發送郵件
            boolean emailSent = emailService.sendEmail(email, "運動場地預約系統重設密碼", "這是運動場地預約系統。請透過新密碼登入之後在您的個人頁面上更改密碼。您的新密碼是: " + newPassword);
            response.put("success", emailSent);
        } else {
            response.put("success", false);
        }

        return response;
    }

    private String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(8);
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";

        // 隨機插入一個大寫字母、小寫字母和數字
        password.append(upperCaseLetters.charAt(random.nextInt(upperCaseLetters.length())));
        password.append(lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));

        for (int i = 3; i < 8; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }
        
        return password.toString();
    }
}