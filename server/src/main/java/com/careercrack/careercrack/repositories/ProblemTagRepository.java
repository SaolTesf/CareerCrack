package com.careercrack.careercrack.repositories;

import com.careercrack.careercrack.models.ProblemTag;
import com.careercrack.careercrack.models.ProblemTagId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProblemTagRepository extends JpaRepository<ProblemTagRepository, ProblemTagId> {
}
