package com.careercrack.careercrack.services;

import com.careercrack.careercrack.models.Problem;
import com.careercrack.careercrack.repositories.ProblemCategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProblemCategoryService {
    private final ProblemCategoryRepository problemCategoryRepository;

    public ProblemCategoryService(ProblemCategoryRepository problemCategoryRepository) {
        this.problemCategoryRepository = problemCategoryRepository;
    }

    public Page<Problem> getAllProblems(String name, Pageable pageable) {
        return problemCategoryRepository.findAllProblemsByName(name, pageable);
    }
}
