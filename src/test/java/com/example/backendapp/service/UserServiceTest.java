package com.example.backendapp.service;

import com.example.backendapp.dto.LoginRequest;
import com.example.backendapp.dto.LoginResponse;
import com.example.backendapp.dto.RegistrationRequest;
import com.example.backendapp.dto.RegistrationResponse;
import com.example.backendapp.entity.User;
import com.example.backendapp.exception.EmailAlreadyExistsException;
import com.example.backendapp.exception.InvalidCredentialsException;
import com.example.backendapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RegistrationRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new RegistrationRequest("John Doe", "john.doe@example.com", "secretPassword123");
    }

    @Test
    @DisplayName("Should successfully register a new user with hashed password when email is not taken")
    void testRegisterUser_Success() {
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secretPassword123")).thenReturn("$2a$10$hashedPasswordValue");

        User savedUser = new User("John Doe", "john.doe@example.com", "$2a$10$hashedPasswordValue");
        savedUser.setId(1L);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        RegistrationResponse response = userService.registerUser(validRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("John Doe", response.getName());
        assertEquals("john.doe@example.com", response.getEmail());
        assertEquals("User registered successfully", response.getMessage());
        assertNotNull(response.getTimestamp());

        verify(userRepository, times(1)).existsByEmail("john.doe@example.com");
        verify(passwordEncoder, times(1)).encode("secretPassword123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when email is already registered")
    void testRegisterUser_DuplicateEmailThrowsException() {
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.registerUser(validRequest)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(userRepository, times(1)).existsByEmail("john.doe@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should successfully login when credentials are valid")
    void testLoginUser_Success() {
        User user = new User("John Doe", "john.doe@example.com", "$2a$10$hashedPasswordValue");
        user.setId(1L);

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secretPassword123", "$2a$10$hashedPasswordValue")).thenReturn(true);

        LoginRequest loginRequest = new LoginRequest("john.doe@example.com", "secretPassword123");
        LoginResponse response = userService.loginUser(loginRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("John Doe", response.getName());
        assertEquals("john.doe@example.com", response.getEmail());
        assertEquals("Login successful", response.getMessage());
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when password does not match")
    void testLoginUser_InvalidPassword() {
        User user = new User("John Doe", "john.doe@example.com", "$2a$10$hashedPasswordValue");

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "$2a$10$hashedPasswordValue")).thenReturn(false);

        LoginRequest loginRequest = new LoginRequest("john.doe@example.com", "wrongPass");
        assertThrows(InvalidCredentialsException.class, () -> userService.loginUser(loginRequest));
    }
}
