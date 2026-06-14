// UserController.java
package com.careercrack.careercrack.controllers;

import com.careercrack.careercrack.dtos.ProblemResponse;
import com.careercrack.careercrack.dtos.UserResponse;
import com.careercrack.careercrack.models.User;
import com.careercrack.careercrack.services.ProblemService;
import com.careercrack.careercrack.services.UserService;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final ProblemService problemService;
    
    public UserController(UserService userService, ProblemService problemService) {
        this.userService = userService;
        this.problemService = problemService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        log.info("Fetched {} users", users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        Optional<UserResponse> user = userService.findById(id);
        log.info("Fetching user with ID: {}", id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ex: /api/users?username=johnDoe
    @GetMapping(params = "username")
    public ResponseEntity<UserResponse> getUserByUserName(@RequestParam("username") String username) {
        Optional<UserResponse> user = userService.findByUserName(username);
        log.info("Fetching user with username: {}", username);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{username}/{email}")
    public ResponseEntity<UserResponse> getUserByUserNameAndEmail(@PathVariable String username, @PathVariable String email) {
        Optional<UserResponse> user = userService.findByUserNameAndEmail(username, email);
        log.info("Fetching user with username: {} and email: {}", username, email);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/email-check/{email}")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
        Boolean exists = userService.existByEmail(email);
        log.info("Email existence check for {}: {}", email, exists);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/username-check/{username}")
    public ResponseEntity<Boolean> existByUsername(@PathVariable String username) {
        Boolean exist = userService.existByUsername(username);
        log.info("Username existence check for {}: {}", username, exist);
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
        log.info("Retrieving {} problems for page {}, sorting by {} and ascending {}, for problem with ID {}", size, page, sortBy, ascending, userId);
        return ResponseEntity.ok(problemService.getAllProblemsByUserId(userId, pageable));
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody User user) {
        UserResponse newUser = userService.createUser(user);
        log.info("Created new user with ID: {}", newUser.getId());
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        UserResponse updatedUser = userService.updateUser(id, user);
        log.info("Updating user with ID: {}", id);
        return (updatedUser != null) ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(id);
        log.info("Deleted user with ID: {}", id);
        return ResponseEntity.ok().build();
    }
}