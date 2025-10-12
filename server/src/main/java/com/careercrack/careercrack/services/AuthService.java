package com.careercrack.careercrack.services;

import com.careercrack.careercrack.models.User;
import com.careercrack.careercrack.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User login(String userName, String password) {
        User user = userRepository.getUserByUsername(userName);
        if(user != null && bCryptPasswordEncoder.matches(password, user.getHashedPassword())) {
            return user;
        }
        return null;
    }
}
