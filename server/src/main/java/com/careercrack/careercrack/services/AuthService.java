package com.careercrack.careercrack.services;

import com.careercrack.careercrack.controllers.AuthController;
import com.careercrack.careercrack.exceptions.InvalidCredentialsException;
import com.careercrack.careercrack.models.User;
import com.careercrack.careercrack.repositories.UserRepository;
import com.careercrack.careercrack.exceptions.DuplicateResourceException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserService userService;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    public User login(String username, String password) {
        User user = userRepository.getUserByUsername(username);
        if(user == null || !passwordEncoder.matches(password, user.getHashedPassword())) {
            throw new InvalidCredentialsException("Username/Email or password was incorrect");
        }
        return null;
    }

    public User register(AuthController.RegisterRequest request) {
        if(userService.existByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email is already in use");
        }
        if(userService.existByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username is already taken");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setHashedPassword(passwordEncoder.encode(request.getPlainPassword()));
        return userService.createUser(user);
    }
}
