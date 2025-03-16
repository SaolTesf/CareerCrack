package com.careercrack.careercrack.services;

import com.careercrack.careercrack.models.UserModel;
import com.careercrack.careercrack.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
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

    public UserModel createUser(UserModel user) {
        return userRepository.save(user);
    }

    public UserModel updateUser(Long id, UserModel user) {
        UserModel existingUser = userRepository.findById(id).orElse(null);
        if (existingUser != null) {
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setUsername(user.getUsername());
            existingUser.setEmail(user.getEmail());
            existingUser.setUpdatedAt(new Date());
            return userRepository.save(existingUser);
        }
        return null;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}