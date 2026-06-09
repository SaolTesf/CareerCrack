// UserController.java
package com.careercrack.careercrack.controllers;

import com.careercrack.careercrack.dtos.ProblemResponse;
import com.careercrack.careercrack.models.User;
import com.careercrack.careercrack.services.ProblemService;
import com.careercrack.careercrack.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final ProblemService problemService;

    // TODO: Use SLF4J instead of this
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, ProblemService problemService) {
        this.userService = userService;
        this.problemService = problemService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        logger.info("Fetched {} users", users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        logger.info("Fetching user with ID: {}", id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ex: /api/users?username=johnDoe
    @GetMapping(params = "username")
    public ResponseEntity<User> getUserByUserName(@RequestParam("username") String username) {
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

    @GetMapping("/{userId}/problems")
    public ResponseEntity<Page<ProblemResponse>> getAllProblemsByUserId(
            @PathVariable (required = true) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        logger.info("Retrieving {} problems for page {}, sorting by {} and ascending {}, for problem with ID {}", size, page, sortBy, ascending, userId);
        return ResponseEntity.ok(problemService.getAllProblemsByUserId(userId, pageable));
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