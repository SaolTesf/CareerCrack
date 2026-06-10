package com.careercrack.careercrack.services;
// TODO: Replace usage of User entity with new User DTO
import com.careercrack.careercrack.dtos.UserResponse;
import com.careercrack.careercrack.mappers.UserMapper;
import com.careercrack.careercrack.models.User;
import com.careercrack.careercrack.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public List<UserResponse> getAllUsers() {
        return userMapper.toDto(userRepository.findAll());
    }

    public Optional<UserResponse> findById(Long id) {
        return userRepository.findById(id).map(userMapper::toDto);
    }

    public Optional<UserResponse> findByUserName(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username)).map(userMapper::toDto);
    }

    public Optional<UserResponse> findByUserNameAndEmail(String username, String email) {
        return Optional.ofNullable(userRepository.findByUsernameAndEmail(username, email)).map(userMapper::toDto);
    }

    public Boolean existByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Boolean existByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public User createUser(User user) {
        user.setHashedPassword(user.getHashedPassword()); // hashing already happens in register service method
        return userRepository.save(user);
    }

    public User updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser != null) {
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setUsername(user.getUsername());
            existingUser.setEmail(user.getEmail());
            existingUser.setHashedPassword(passwordEncoder.encode(user.getHashedPassword()));
            return userRepository.save(existingUser);
        }
        return null;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}