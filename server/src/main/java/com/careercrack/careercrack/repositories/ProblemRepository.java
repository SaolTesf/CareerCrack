package com.careercrack.careercrack.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.careercrack.careercrack.models.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
    Page<Problem> findAllByUserId(Long userId, Pageable pageable);
}
