package com.careercrack.careercrack.services;

import com.careercrack.careercrack.dtos.ProblemResponse;
import com.careercrack.careercrack.enums.Category;
import com.careercrack.careercrack.mappers.ProblemMapper;
import com.careercrack.careercrack.models.Problem;
import com.careercrack.careercrack.models.ProblemCategory;
import com.careercrack.careercrack.repositories.ProblemCategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProblemCategoryService {
    private final ProblemCategoryRepository problemCategoryRepository;
    private final ProblemMapper problemMapper;

    public ProblemCategoryService(ProblemCategoryRepository problemCategoryRepository,  ProblemMapper problemMapper) {
        this.problemCategoryRepository = problemCategoryRepository;
        this.problemMapper = problemMapper;
    }

    public Page<ProblemResponse> getAllProblems(String name, Pageable pageable) {
        Page<Problem> problems = problemCategoryRepository.findAllProblemsByName(name, pageable);
        return problems.map(problemMapper::toDto);
    }

    public Optional<ProblemCategory> findById(Long id) {
        return problemCategoryRepository.findById(id);
    }

    public Optional<ProblemCategory> findByName(Category name) {
        return problemCategoryRepository.findProblemCategoryByName(name);
    }
}
