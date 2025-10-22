package com.careercrack.careercrack.controllers;

import com.careercrack.careercrack.services.JwtService;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.careercrack.careercrack.services.AuthService;
import com.careercrack.careercrack.models.User;

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
        private String username;
        private String password;
    }

    @Getter
    @Setter
    public static class AuthResponse {
        private String token;
        private String username;
        private String email;

        public AuthResponse(String token, User user) {
            this.token = token;
            this.username = user.getUsername();
            this.email = user.getEmail();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = authService.login(loginRequest.getUsername(), loginRequest.getPassword());
        if(user != null) {
            String token = jwtService.generateToken(user.getUsername());
            logger.info("User {} logged in successfully", loginRequest.getUsername());
            return ResponseEntity.ok(new AuthResponse(token, user));
        }
        logger.warn("Failed login attempt for username: {}", loginRequest.getUsername());
        return ResponseEntity.status(401).body("invalid Credentials");
    }

}
