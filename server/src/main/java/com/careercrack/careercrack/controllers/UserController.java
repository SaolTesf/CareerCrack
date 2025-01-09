// UserController.java
package com.careercrack.careercrack.controllers;

import com.careercrack.careercrack.models.UserModel;
import com.careercrack.careercrack.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@Controller
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

    @GetMapping("/{id}")
    public ResponseEntity<UserModel> getUserById(@PathVariable Long id) {
        Optional<UserModel> user = userService.findById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{userName}")
    public ResponseEntity<UserModel> getUserByUserName(@PathVariable String userName) {
        UserModel user = userService.findByUserName(userName);
        return (user != null) ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @GetMapping("/username/{userName}/email/{email}")
    public ResponseEntity<UserModel> getUserByUserNameAndEmail(@PathVariable String userName, @PathVariable String email) {
        UserModel user = userService.findByUserNameAndEmail(userName, email);
        return (user != null) ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @GetMapping("/existsByEmail/{email}")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
        Boolean exists = userService.existByEmail(email);
        return ResponseEntity.ok(exists);
    }

    @PostMapping
    public ResponseEntity<Void> createUser(@Valid @RequestBody UserModel user) {
        userService.createUser(user);
        return ResponseEntity.ok().build();
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