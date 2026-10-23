package com.example.backendapp.controller;

import com.example.backendapp.dto.LoginRequest;
import com.example.backendapp.dto.LoginResponse;
import com.example.backendapp.dto.RegistrationRequest;
import com.example.backendapp.dto.RegistrationResponse;
import com.example.backendapp.exception.EmailAlreadyExistsException;
import com.example.backendapp.exception.GlobalExceptionHandler;
import com.example.backendapp.exception.InvalidCredentialsException;
import com.example.backendapp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("POST /api/users/register - Should return 201 Created on valid request")
    void testRegisterUser_Success() throws Exception {
        RegistrationRequest request = new RegistrationRequest("Alice Smith", "alice@example.com", "securePass123");
        RegistrationResponse response = new RegistrationResponse(1L, "Alice Smith", "alice@example.com", "User registered successfully");

        when(userService.registerUser(any(RegistrationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Alice Smith"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("POST /api/users/register - Should return 409 Conflict when email already exists")
    void testRegisterUser_EmailAlreadyExists() throws Exception {
        RegistrationRequest request = new RegistrationRequest("Alice Smith", "alice@example.com", "securePass123");

        when(userService.registerUser(any(RegistrationRequest.class)))
                .thenThrow(new EmailAlreadyExistsException("A user with email 'alice@example.com' already exists"));

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("A user with email 'alice@example.com' already exists"))
                .andExpect(jsonPath("$.path").value("/api/users/register"));
    }

    @Test
    @DisplayName("POST /api/users/register - Should return 400 Bad Request when validation fails")
    void testRegisterUser_ValidationFailure() throws Exception {
        RegistrationRequest invalidRequest = new RegistrationRequest("", "invalid-email-format", "123");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.validationErrors.name").exists())
                .andExpect(jsonPath("$.validationErrors.email").exists())
                .andExpect(jsonPath("$.validationErrors.password").exists());
    }

    @Test
    @DisplayName("POST /api/users/login - Should return 200 OK on valid credentials")
    void testLoginUser_Success() throws Exception {
        LoginRequest request = new LoginRequest("alice@example.com", "securePass123");
        LoginResponse response = new LoginResponse(1L, "Alice Smith", "alice@example.com", "Login successful");

        when(userService.loginUser(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Alice Smith"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    @DisplayName("POST /api/users/login - Should return 401 Unauthorized on invalid credentials")
    void testLoginUser_InvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("alice@example.com", "wrongPassword");

        when(userService.loginUser(any(LoginRequest.class)))
                .thenThrow(new InvalidCredentialsException("Invalid email or password"));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }
}
