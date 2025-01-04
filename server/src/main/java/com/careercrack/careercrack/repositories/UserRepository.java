package com.careercrack.careercrack.repositories;

import com.careercrack.careercrack.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserModel, Long> {
    UserModel findByUsername(String username);
    UserModel findByUsernameAndEmail(String username, String email);
    boolean existsByEmail(String email);
}
