package com.careercrack.careercrack.services;

import com.careercrack.careercrack.models.UserModel;
import com.careercrack.careercrack.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserModel> findById(Long id) {
        return userRepository.findById(id);
    }

    public UserModel findByUserName(String userName) {
        return userRepository.findByUsername(userName);
    }

    public UserModel findByUserNameAndEmail(String userName, String email) {
        return userRepository.findByUsernameAndEmail(userName, email);
    }

    public Boolean existByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void createUser(UserModel user) {
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }
}