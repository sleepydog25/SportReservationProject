package com.example.demo.modal.dto;

public class ReservationDto {
	private Long id;
    private String email;
    private String field;
    private String date;
    private String time;
    private boolean equipment;

    // Getters and setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isEquipment() {
        return equipment;
    }

    public void setEquipment(boolean equipment) {
        this.equipment = equipment;
    }
    
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
    
    @Override
    public String toString() {
        return "ReservationDto{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", equipment=" + equipment +
                ", field='" + field + '\'' +
                '}';
    }
}

//創建一個 DTO 類來接收前端發送的數據。