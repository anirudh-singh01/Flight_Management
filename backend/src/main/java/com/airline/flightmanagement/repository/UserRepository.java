package com.airline.flightmanagement.repository;

import com.airline.flightmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by username
     */
    Optional<User> findByUserName(String userName);
    
    /**
     * Find user by email
     */
    Optional<User> findByEmailId(String emailId);
    
    /**
     * Check if username exists
     */
    boolean existsByUserName(String userName);
    
    /**
     * Check if email exists
     */
    boolean existsByEmailId(String emailId);
}
