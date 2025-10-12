package com.careercrack.careercrack.controllers;

import lombok.Getter;
import lombok.Setter;
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

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Getter
    @Setter
    public static class LoginRequest {
        private String userName;
        private String password;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = authService.login(loginRequest.getUserName(), loginRequest.getPassword());
        if(user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(401).body("invalid Credentials");
    }

}
