package com.example.backendapp.service;

import com.example.backendapp.dto.LoginRequest;
import com.example.backendapp.dto.LoginResponse;
import com.example.backendapp.dto.RegistrationRequest;
import com.example.backendapp.dto.RegistrationResponse;
import com.example.backendapp.entity.User;
import com.example.backendapp.exception.EmailAlreadyExistsException;
import com.example.backendapp.exception.InvalidCredentialsException;
import com.example.backendapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new user after verifying that the email does not already exist.
     * The password is securely hashed using BCrypt before persistence.
     *
     * @param request the registration request containing name, email, and password
     * @return registration response with created user details
     * @throws EmailAlreadyExistsException if the email is already registered
     */
    @Transactional
    public RegistrationResponse registerUser(RegistrationRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        // Prevent duplicate email registrations
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExistsException("A user with email '" + normalizedEmail + "' already exists");
        }

        // Create new User entity with BCrypt hashed password
        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        return new RegistrationResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                "User registered successfully"
        );
    }

    /**
     * Authenticate a user by verifying email existence and checking the BCrypt password hash.
     *
     * @param request the login request containing email and plain-text password
     * @return login response with user details
     * @throws InvalidCredentialsException if email is not found or password does not match
     */
    @Transactional(readOnly = true)
    public LoginResponse loginUser(LoginRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                "Login successful"
        );
    }
}
