package com.airline.flightmanagement.service;

import com.airline.flightmanagement.dto.UserDTO;
import com.airline.flightmanagement.dto.LoginResponseDTO;
import com.airline.flightmanagement.entity.User;
import com.airline.flightmanagement.entity.UserRole;
import com.airline.flightmanagement.entity.CustomerCategory;
import com.airline.flightmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Register a new user
     */
    public UserDTO registerUser(UserDTO userDTO) {
        // Check if username already exists
        if (userRepository.existsByUserName(userDTO.getUserName())) {
            throw new RuntimeException("Username already exists: " + userDTO.getUserName());
        }
        
        // Check if email already exists
        if (userRepository.existsByEmailId(userDTO.getEmailId())) {
            throw new RuntimeException("Email already exists: " + userDTO.getEmailId());
        }
        
        // Set default role to CUSTOMER if not specified
        if (userDTO.getRole() == null) {
            userDTO.setRole(UserRole.CUSTOMER);
        }
        
        // Set default customer category to REGULAR if not specified
        if (userDTO.getCustomerCategory() == null) {
            userDTO.setCustomerCategory(CustomerCategory.REGULAR);
        }
        
        // Create User entity from DTO
        User user = new User();
        user.setUserName(userDTO.getUserName());
        user.setPassword(userDTO.getPassword()); // In production, this should be encrypted
        user.setRole(userDTO.getRole());
        user.setCustomerCategory(userDTO.getCustomerCategory());
        user.setPhone(userDTO.getPhone());
        user.setEmailId(userDTO.getEmailId());
        user.setAddress1(userDTO.getAddress1());
        user.setAddress2(userDTO.getAddress2());
        user.setCity(userDTO.getCity());
        user.setState(userDTO.getState());
        user.setZipCode(userDTO.getZipCode());
        user.setDob(userDTO.getDob());
        
        // Save user to database
        User savedUser = userRepository.save(user);
        
        // Convert back to DTO and return
        return convertToDTO(savedUser);
    }
    
    /**
     * Register a new administrator
     */
    public UserDTO registerAdmin(UserDTO userDTO) {
        // Check if username already exists
        if (userRepository.existsByUserName(userDTO.getUserName())) {
            throw new RuntimeException("Username already exists: " + userDTO.getUserName());
        }
        
        // Check if email already exists
        if (userRepository.existsByEmailId(userDTO.getEmailId())) {
            throw new RuntimeException("Email already exists: " + userDTO.getEmailId());
        }
        
        // Force role to ADMIN for administrator registration
        userDTO.setRole(UserRole.ADMIN);
        
        // Set customer category to null for administrators (not applicable)
        userDTO.setCustomerCategory(null);
        
        // Create User entity from DTO
        User user = new User();
        user.setUserName(userDTO.getUserName());
        user.setPassword(userDTO.getPassword()); // In production, this should be encrypted
        user.setRole(userDTO.getRole());
        user.setCustomerCategory(userDTO.getCustomerCategory());
        user.setPhone(userDTO.getPhone());
        user.setEmailId(userDTO.getEmailId());
        user.setAddress1(userDTO.getAddress1());
        user.setAddress2(userDTO.getAddress2());
        user.setCity(userDTO.getCity());
        user.setState(userDTO.getState());
        user.setZipCode(userDTO.getZipCode());
        user.setDob(userDTO.getDob());
        
        // Save user to database
        User savedUser = userRepository.save(user);
        
        // Convert back to DTO and return
        return convertToDTO(savedUser);
    }
    
    /**
     * Get user by ID
     */
    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return convertToDTO(user);
    }
    
    /**
     * Get user by username
     */
    public UserDTO getUserByUsername(String userName) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + userName));
        return convertToDTO(user);
    }
    
    /**
     * Get all users
     */
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Update user
     */
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        // Update fields
        existingUser.setUserName(userDTO.getUserName());
        existingUser.setPhone(userDTO.getPhone());
        existingUser.setEmailId(userDTO.getEmailId());
        existingUser.setAddress1(userDTO.getAddress1());
        existingUser.setAddress2(userDTO.getAddress2());
        existingUser.setCity(userDTO.getCity());
        existingUser.setState(userDTO.getState());
        existingUser.setZipCode(userDTO.getZipCode());
        existingUser.setDob(userDTO.getDob());
        existingUser.setCustomerCategory(userDTO.getCustomerCategory());
        
        User updatedUser = userRepository.save(existingUser);
        return convertToDTO(updatedUser);
    }
    
    /**
     * Delete user
     */
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        userRepository.deleteById(userId);
    }
    
    /**
     * Validate user login credentials
     */
    public LoginResponseDTO validateLogin(String userName, String password) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
        
        // Check if password matches
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid username or password");
        }
        
        // Return login response with user role
        return new LoginResponseDTO(
            user.getUserId(),
            user.getUserName(),
            user.getRole(),
            "Login successful"
        );
    }
    
    /**
     * Convert User entity to UserDTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setUserName(user.getUserName());
        dto.setPassword(user.getPassword());
        dto.setRole(user.getRole());
        dto.setCustomerCategory(user.getCustomerCategory());
        dto.setPhone(user.getPhone());
        dto.setEmailId(user.getEmailId());
        dto.setAddress1(user.getAddress1());
        dto.setAddress2(user.getAddress2());
        dto.setCity(user.getCity());
        dto.setState(user.getState());
        dto.setZipCode(user.getZipCode());
        dto.setDob(user.getDob());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
