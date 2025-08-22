package com.airline.flightmanagement.dto;

import com.airline.flightmanagement.entity.UserRole;

public class LoginResponseDTO {
    
    private Long userId;
    private String userName;
    private UserRole role;
    private String message;
    
    // Default constructor
    public LoginResponseDTO() {}
    
    // Constructor with fields
    public LoginResponseDTO(Long userId, String userName, UserRole role, String message) {
        this.userId = userId;
        this.userName = userName;
        this.role = role;
        this.message = message;
    }
    
    // Getters and Setters
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return "LoginResponseDTO{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", role=" + role +
                ", message='" + message + '\'' +
                '}';
    }
}
