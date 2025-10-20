// UserController.java
package com.careercrack.careercrack.controllers;

import com.careercrack.careercrack.models.User;
import com.careercrack.careercrack.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/users")
public class UserController {
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        logger.info("Fetched {} users", users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        logger.info("Fetching user with ID: {}", id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUserName(@PathVariable String username) {
        User user = userService.findByUserName(username);
        logger.info("Fetching user with username: {}", username);
        return (user != null) ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{username}/{email}")
    public ResponseEntity<User> getUserByUserNameAndEmail(@PathVariable String username, @PathVariable String email) {
        User user = userService.findByUserNameAndEmail(username, email);
        logger.info("Fetching user with username: {} and email: {}", username, email);
        return (user != null) ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @GetMapping("/email-check/{email}")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
        Boolean exists = userService.existByEmail(email);
        logger.info("Email existence check for {}: {}", email, exists);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/username-check/{username}")
    public ResponseEntity<Boolean> existByUsername(@PathVariable String username) {
        Boolean exist = userService.existByUsername(username);
        logger.info("Username existence check for {}: {}", username, exist);
        return ResponseEntity.ok(exist);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User newUser = userService.createUser(user);
        logger.info("Created new user with ID: {}", newUser.getId());
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        logger.info("Updating user with ID: {}", id);
        return (updatedUser != null) ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(id);
        logger.info("Deleted user with ID: {}", id);
        return ResponseEntity.ok().build();
    }
}