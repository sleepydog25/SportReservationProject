package com.example.demo.modal.dto;

import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {
    private int id;    
    private String uuid;
    private String name;
    private String gender;
    private String birthday;
    private String phone;
    private String email;
    private String password;
    private boolean admin;

    {
        // 註冊時自動生成UUID
        this.uuid = UUID.randomUUID().toString();
    }
}


//import java.util.UUID;
//
//public class User {
//    private int id;    
//    private String uuid;
//    private String name;
//    private String gender;
//    private String birthday;
//    private String phone;
//    private String email;
//    private String password;
//    private boolean admin;
//
//    public User() {
//        // 註冊時自動生成UUID
//        this.uuid = UUID.randomUUID().toString();
//    }
//
//    // getters and setters
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getUuid() {
//        return uuid;
//    }
//
//    public void setUuid(String uuid) {
//        this.uuid = uuid;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getGender() {
//        return gender;
//    }
//
//    public void setGender(String gender) {
//        this.gender = gender;
//    }
//
//    public String getBirthday() {
//        return birthday;
//    }
//
//    public void setBirthday(String birthday) {
//        this.birthday = birthday;
//    }
//
//    public String getPhone() {
//        return phone;
//    }
//
//    public void setPhone(String phone) {
//        this.phone = phone;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//    
//    public boolean isAdmin() {
//        return admin;
//    }
//
//    public void setAdmin(boolean admin) {
//        this.admin = admin;
//    }
//}