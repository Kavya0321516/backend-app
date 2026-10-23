package com.example.backendapp.repository;

import com.example.backendapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Check if a user with the given email already exists.
     *
     * @param email user email address
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find a user by their email address.
     *
     * @param email user email address
     * @return Optional containing User if found
     */
    Optional<User> findByEmail(String email);
}
