package com.example.demo.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()
            );
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
}

    
    
    // 失敗的加密方法
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new Pbkdf2PasswordEncoder(
//            "", // secret (empty, as not used in the JavaScript example)
//            16, // salt length (16 bytes for 128-bit salt)
//            1000, // iterations (same as in the JavaScript example)
//            Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256 // algorithm
//        );
//    }
    
//    // Encrypt the password using PBKDF2
//    const salt = CryptoJS.lib.WordArray.random(128 / 8).toString(); // Generate random salt
//    const iterations = 1000;
//    const keySize = 256 / 32;
//    //const hashedPassword = CryptoJS.PBKDF2(password, salt, {
//    //    keySize: keySize,
//    //    iterations: iterations
//    //}).toString();
//    
//    const hashedPassword = CryptoJS.PBKDF2(password, saltWordArray, {
//        keySize: 256 / 32,
//        iterations: 1000,
//        hasher: CryptoJS.algo.SHA256
//    });


