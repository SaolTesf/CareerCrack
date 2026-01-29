package com.careercrack.careercrack.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.careercrack.careercrack.models.Problem;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

}
