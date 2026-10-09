package com.careercrack.careercrack.controllers;

import com.careercrack.careercrack.dtos.UserResponse;
import com.careercrack.careercrack.services.JwtService;
import com.careercrack.careercrack.services.AuthService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @Getter
    @Setter
    public static class LoginRequest {
        @NotBlank(message = "Username or email is required")
        private String identifier;

        @NotBlank(message = "Password is required")
        private String password;
    }

    @Getter
    @Setter
    public static class RegisterRequest {

        @NotBlank(message = "First name is required")
        @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
        private String firstName;

        @NotBlank(message = "Last name is required")
        @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
        private String lastName;

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        private String username;

        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        private String plainPassword;
    }

    @Getter
    @Setter
    public static class AuthResponse {
        private String token;
        private UserResponse user;

        public AuthResponse(String token, UserResponse user) {
            this.token = token;
            this.user = user;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        UserResponse user = authService.login(request.getIdentifier(), request.getPassword());
        String token = jwtService.generateToken(user.getUsername());
        logger.info("User {} logged in successfully", request.getIdentifier());
        return ResponseEntity.ok(new AuthResponse(token, user));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse user = authService.register(request); // throws an error that will be caught so no need for null check here
        String token = jwtService.generateToken(user.getUsername());
        logger.info("User {} created successfully. Initiating Login", user.getUsername());
        return ResponseEntity.ok(new AuthResponse(token, user));
    }

}
