package com.careercrack.careercrack.services;

import com.careercrack.careercrack.enums.Category;
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

    public ProblemCategoryService(ProblemCategoryRepository problemCategoryRepository) {
        this.problemCategoryRepository = problemCategoryRepository;
    }

    public Page<Problem> getAllProblems(String name, Pageable pageable) {
        return problemCategoryRepository.findAllProblemsByName(name, pageable);
    }

    public Optional<ProblemCategory> findById(Long id) {
        return problemCategoryRepository.findById(id);
    }

    public Optional<ProblemCategory> findByName(Category name) {
        return problemCategoryRepository.findProblemCategoryByName(name);
    }
}
