package com.example.backendapp.controller;

import com.example.backendapp.dto.LoginRequest;
import com.example.backendapp.dto.LoginResponse;
import com.example.backendapp.dto.RegistrationRequest;
import com.example.backendapp.dto.RegistrationResponse;
import com.example.backendapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * User registration REST endpoint.
     * POST /api/users/register
     *
     * @param request validated registration payload
     * @return 201 CREATED with RegistrationResponse
     */
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerUser(@Valid @RequestBody RegistrationRequest request) {
        RegistrationResponse response = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * User login REST endpoint.
     * POST /api/users/login
     *
     * @param request validated login payload
     * @return 200 OK with LoginResponse
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.loginUser(request);
        return ResponseEntity.ok(response);
    }
}
