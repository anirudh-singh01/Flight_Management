package com.airline.flightmanagement.service;

import com.airline.flightmanagement.dto.LoginResponseDTO;
import com.airline.flightmanagement.entity.User;
import com.airline.flightmanagement.entity.UserRole;
import com.airline.flightmanagement.entity.CustomerCategory;
import com.airline.flightmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUserName("testuser");
        testUser.setPassword("password123");
        testUser.setRole(UserRole.CUSTOMER);
        testUser.setCustomerCategory(CustomerCategory.REGULAR);
        testUser.setPhone("+1234567890");
        testUser.setEmailId("test@example.com");
        testUser.setAddress1("123 Test St");
        testUser.setCity("Test City");
        testUser.setState("Test State");
        testUser.setZipCode("12345");
        testUser.setDob(LocalDate.of(1990, 1, 1));
    }

    @Test
    void testValidateLogin_ValidCredentials_ReturnsLoginResponse() {
        // Arrange
        String userName = "testuser";
        String password = "password123";
        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(testUser));

        // Act
        LoginResponseDTO result = userService.validateLogin(userName, password);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getUserId(), result.getUserId());
        assertEquals(testUser.getUserName(), result.getUserName());
        assertEquals(testUser.getRole(), result.getRole());
        assertEquals("Login successful", result.getMessage());
    }

    @Test
    void testValidateLogin_InvalidUsername_ThrowsException() {
        // Arrange
        String userName = "nonexistentuser";
        String password = "password123";
        when(userRepository.findByUserName(userName)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.validateLogin(userName, password));
        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void testValidateLogin_InvalidPassword_ThrowsException() {
        // Arrange
        String userName = "testuser";
        String password = "wrongpassword";
        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(testUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.validateLogin(userName, password));
        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void testValidateLogin_AdminUser_ReturnsCorrectRole() {
        // Arrange
        testUser.setRole(UserRole.ADMIN);
        String userName = "adminuser";
        String password = "password123";
        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(testUser));

        // Act
        LoginResponseDTO result = userService.validateLogin(userName, password);

        // Assert
        assertNotNull(result);
        assertEquals(UserRole.ADMIN, result.getRole());
        assertEquals("Login successful", result.getMessage());
    }
}
