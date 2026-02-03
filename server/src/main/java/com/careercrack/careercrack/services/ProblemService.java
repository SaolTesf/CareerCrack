package com.careercrack.careercrack.services;

import com.careercrack.careercrack.repositories.ProblemRepository;
import com.careercrack.careercrack.models.Problem;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProblemService {
    private final ProblemRepository problemRepository;

    public ProblemService(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    public List<Problem> getAllProblems() {
        return problemRepository.findAll();
    }

    public Optional<Problem> findById(Long id) {
        return problemRepository.findById(id);
    }
}
