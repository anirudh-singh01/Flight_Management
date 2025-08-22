package com.airline.flightmanagement.dto;

import com.airline.flightmanagement.entity.CustomerCategory;
import com.airline.flightmanagement.entity.UserRole;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class UserDTO {
    
    private Long userId;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String userName;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    @NotNull(message = "Role is required")
    private UserRole role;
    
    @NotNull(message = "Customer category is required")
    private CustomerCategory customerCategory;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phone;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String emailId;
    
    @NotBlank(message = "Address line 1 is required")
    private String address1;
    
    private String address2;
    
    @NotBlank(message = "City is required")
    private String city;
    
    @NotBlank(message = "State is required")
    private String state;
    
    @NotBlank(message = "ZIP code is required")
    @Pattern(regexp = "^\\d{5}(-\\d{4})?$", message = "Invalid ZIP code format")
    private String zipCode;
    
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dob;
    
    private LocalDate createdAt;
    
    // Default constructor
    public UserDTO() {}
    
    // Constructor with all fields
    public UserDTO(Long userId, String userName, String password, UserRole role, 
                   CustomerCategory customerCategory, String phone, String emailId, 
                   String address1, String address2, String city, String state, 
                   String zipCode, LocalDate dob, LocalDate createdAt) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.customerCategory = customerCategory;
        this.phone = phone;
        this.emailId = emailId;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.dob = dob;
        this.createdAt = createdAt;
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
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    public CustomerCategory getCustomerCategory() {
        return customerCategory;
    }
    
    public void setCustomerCategory(CustomerCategory customerCategory) {
        this.customerCategory = customerCategory;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmailId() {
        return emailId;
    }
    
    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
    
    public String getAddress1() {
        return address1;
    }
    
    public void setAddress1(String address1) {
        this.address1 = address1;
    }
    
    public String getAddress2() {
        return address2;
    }
    
    public void setAddress2(String address2) {
        this.address2 = address2;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getZipCode() {
        return zipCode;
    }
    
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    
    public LocalDate getDob() {
        return dob;
    }
    
    public void setDob(LocalDate dob) {
        this.dob = dob;
    }
    
    public LocalDate getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "UserDTO{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", role=" + role +
                ", customerCategory=" + customerCategory +
                ", phone='" + phone + '\'' +
                ", emailId='" + emailId + '\'' +
                ", address1='" + address1 + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", dob=" + dob +
                ", createdAt=" + createdAt +
                '}';
    }
}
