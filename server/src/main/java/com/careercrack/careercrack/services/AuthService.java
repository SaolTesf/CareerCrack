package com.careercrack.careercrack.services;

import com.careercrack.careercrack.controllers.AuthController;
import com.careercrack.careercrack.models.User;
import com.careercrack.careercrack.repositories.UserRepository;
import com.careercrack.careercrack.exceptions.DuplicateResourceException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserService userService;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, UserService userService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userService = userService;
    }

    public User login(String username, String password) {
        User user = userRepository.getUserByUsername(username);
        if(user != null && bCryptPasswordEncoder.matches(password, user.getHashedPassword())) {
            return user;
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
        return userService.createUser(user);
    }
}
