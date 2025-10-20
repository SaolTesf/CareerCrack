package com.careercrack.careercrack.repositories;

import com.careercrack.careercrack.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByUsernameAndEmail(String username, String email);

    User getUserByUsername(String username);
    User getUserByEmail(String email);
    boolean existsByEmail(String email);

    Boolean existsByUsername(String username);
}
