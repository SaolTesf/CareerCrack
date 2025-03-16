// UserController.java
package com.careercrack.careercrack.controllers;

import com.careercrack.careercrack.models.UserModel;
import com.careercrack.careercrack.services.UserService;
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

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserModel>> getAllUsers() {
        List<UserModel> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<UserModel> getUserById(@PathVariable Long id) {
        Optional<UserModel> user = userService.findById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{userName}")
    public ResponseEntity<UserModel> getUserByUserName(@PathVariable String userName) {
        UserModel user = userService.findByUserName(userName);
        return (user != null) ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{userName}/{email}")
    public ResponseEntity<UserModel> getUserByUserNameAndEmail(@PathVariable String userName, @PathVariable String email) {
        UserModel user = userService.findByUserNameAndEmail(userName, email);
        return (user != null) ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @GetMapping("/email-check/{email}")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
        Boolean exists = userService.existByEmail(email);
        return ResponseEntity.ok(exists);
    }

    @PostMapping
    public ResponseEntity<UserModel> createUser(@Valid @RequestBody UserModel user) {
        UserModel newUser = userService.createUser(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserModel> updateUser(@PathVariable Long id, @Valid @RequestBody UserModel user) {
        UserModel updatedUser = userService.updateUser(id, user);
        return (updatedUser != null) ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}