package com.example.catalog_service.repo;

import com.example.catalog_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for User entities.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Find a user by their username.
     */
    Optional<User> findByUsername(String username);
}
