package com.airline.flightmanagement.service;

import com.airline.flightmanagement.dto.UserDTO;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    private UserDTO testUserDTO;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUserDTO = new UserDTO();
        testUserDTO.setUserName("testuser");
        testUserDTO.setPassword("password123");
        testUserDTO.setRole(UserRole.CUSTOMER);
        testUserDTO.setCustomerCategory(CustomerCategory.REGULAR);
        testUserDTO.setPhone("+1234567890");
        testUserDTO.setEmailId("test@example.com");
        testUserDTO.setAddress1("123 Test St");
        testUserDTO.setCity("Test City");
        testUserDTO.setState("Test State");
        testUserDTO.setZipCode("12345");
        testUserDTO.setDob(LocalDate.of(1990, 1, 1));
        
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
        testUser.setCreatedAt(LocalDate.now());
    }
    
    @Test
    void testRegisterUser_Success() {
        // Arrange
        when(userRepository.existsByUserName("testuser")).thenReturn(false);
        when(userRepository.existsByEmailId("test@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // Act
        UserDTO result = userService.registerUser(testUserDTO);
        
        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUserName());
        assertEquals("test@example.com", result.getEmailId());
        assertEquals(UserRole.CUSTOMER, result.getRole());
        assertEquals(CustomerCategory.REGULAR, result.getCustomerCategory());
        
        verify(userRepository).existsByUserName("testuser");
        verify(userRepository).existsByEmailId("test@example.com");
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void testRegisterUser_UsernameAlreadyExists() {
        // Arrange
        when(userRepository.existsByUserName("testuser")).thenReturn(true);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(testUserDTO);
        });
        
        assertEquals("Username already exists: testuser", exception.getMessage());
        
        verify(userRepository).existsByUserName("testuser");
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void testRegisterUser_EmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByUserName("testuser")).thenReturn(false);
        when(userRepository.existsByEmailId("test@example.com")).thenReturn(true);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(testUserDTO);
        });
        
        assertEquals("Email already exists: test@example.com", exception.getMessage());
        
        verify(userRepository).existsByUserName("testuser");
        verify(userRepository).existsByEmailId("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void testRegisterAdmin_Success() {
        // Arrange
        UserDTO adminDTO = new UserDTO();
        adminDTO.setUserName("adminuser");
        adminDTO.setPassword("adminpass123");
        adminDTO.setPhone("+1234567890");
        adminDTO.setEmailId("admin@example.com");
        adminDTO.setAddress1("456 Admin St");
        adminDTO.setCity("Admin City");
        adminDTO.setState("Admin State");
        adminDTO.setZipCode("54321");
        adminDTO.setDob(LocalDate.of(1985, 5, 15));
        
        User adminUser = new User();
        adminUser.setUserId(2L);
        adminUser.setUserName("adminuser");
        adminUser.setPassword("adminpass123");
        adminUser.setRole(UserRole.ADMIN);
        adminUser.setCustomerCategory(null);
        adminUser.setPhone("+1234567890");
        adminUser.setEmailId("admin@example.com");
        adminUser.setAddress1("456 Admin St");
        adminUser.setCity("Admin City");
        adminUser.setState("Admin State");
        adminUser.setZipCode("54321");
        adminUser.setDob(LocalDate.of(1985, 5, 15));
        adminUser.setCreatedAt(LocalDate.now());
        
        when(userRepository.existsByUserName("adminuser")).thenReturn(false);
        when(userRepository.existsByEmailId("admin@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(adminUser);
        
        // Act
        UserDTO result = userService.registerAdmin(adminDTO);
        
        // Assert
        assertNotNull(result);
        assertEquals("adminuser", result.getUserName());
        assertEquals("admin@example.com", result.getEmailId());
        assertEquals(UserRole.ADMIN, result.getRole());
        assertNull(result.getCustomerCategory()); // Admin should not have customer category
        
        verify(userRepository).existsByUserName("adminuser");
        verify(userRepository).existsByEmailId("admin@example.com");
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void testRegisterAdmin_UsernameAlreadyExists() {
        // Arrange
        UserDTO adminDTO = new UserDTO();
        adminDTO.setUserName("existingadmin");
        adminDTO.setPassword("adminpass123");
        adminDTO.setPhone("+1234567890");
        adminDTO.setEmailId("admin@example.com");
        adminDTO.setAddress1("456 Admin St");
        adminDTO.setCity("Admin City");
        adminDTO.setState("Admin State");
        adminDTO.setZipCode("54321");
        adminDTO.setDob(LocalDate.of(1985, 5, 15));
        
        when(userRepository.existsByUserName("existingadmin")).thenReturn(true);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerAdmin(adminDTO);
        });
        
        assertEquals("Username already exists: existingadmin", exception.getMessage());
        
        verify(userRepository).existsByUserName("existingadmin");
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void testRegisterAdmin_EmailAlreadyExists() {
        // Arrange
        UserDTO adminDTO = new UserDTO();
        adminDTO.setUserName("newadmin");
        adminDTO.setPassword("adminpass123");
        adminDTO.setPhone("+1234567890");
        adminDTO.setEmailId("existing@example.com");
        adminDTO.setAddress1("456 Admin St");
        adminDTO.setCity("Admin City");
        adminDTO.setState("Admin State");
        adminDTO.setZipCode("54321");
        adminDTO.setDob(LocalDate.of(1985, 5, 15));
        
        when(userRepository.existsByUserName("newadmin")).thenReturn(false);
        when(userRepository.existsByEmailId("existing@example.com")).thenReturn(true);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerAdmin(adminDTO);
        });
        
        assertEquals("Email already exists: existing@example.com", exception.getMessage());
        
        verify(userRepository).existsByUserName("newadmin");
        verify(userRepository).existsByEmailId("existing@example.com");
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void testRegisterUser_DefaultValues() {
        // Arrange
        UserDTO userDTOWithoutDefaults = new UserDTO();
        userDTOWithoutDefaults.setUserName("newuser");
        userDTOWithoutDefaults.setPassword("password123");
        userDTOWithoutDefaults.setPhone("+1234567890");
        userDTOWithoutDefaults.setEmailId("new@example.com");
        userDTOWithoutDefaults.setAddress1("123 New St");
        userDTOWithoutDefaults.setCity("New City");
        userDTOWithoutDefaults.setState("New State");
        userDTOWithoutDefaults.setZipCode("54321");
        userDTOWithoutDefaults.setDob(LocalDate.of(1995, 5, 15));
        
        User savedUser = new User();
        savedUser.setUserId(2L);
        savedUser.setUserName("newuser");
        savedUser.setPassword("password123");
        savedUser.setRole(UserRole.CUSTOMER);
        savedUser.setCustomerCategory(CustomerCategory.REGULAR);
        savedUser.setPhone("+1234567890");
        savedUser.setEmailId("new@example.com");
        savedUser.setAddress1("123 New St");
        savedUser.setCity("New City");
        savedUser.setState("New State");
        savedUser.setZipCode("54321");
        savedUser.setDob(LocalDate.of(1995, 5, 15));
        savedUser.setCreatedAt(LocalDate.now());
        
        when(userRepository.existsByUserName("newuser")).thenReturn(false);
        when(userRepository.existsByEmailId("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        // Act
        UserDTO result = userService.registerUser(userDTOWithoutDefaults);
        
        // Assert
        assertNotNull(result);
        assertEquals(UserRole.CUSTOMER, result.getRole());
        assertEquals(CustomerCategory.REGULAR, result.getCustomerCategory());
        
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void testGetUserById_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        // Act
        UserDTO result = userService.getUserById(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("testuser", result.getUserName());
        
        verify(userRepository).findById(1L);
    }
    
    @Test
    void testGetUserById_NotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserById(999L);
        });
        
        assertEquals("User not found with ID: 999", exception.getMessage());
        
        verify(userRepository).findById(999L);
    }
}
