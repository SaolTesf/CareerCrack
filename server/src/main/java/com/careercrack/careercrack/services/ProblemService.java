package com.careercrack.careercrack.services;

import com.careercrack.careercrack.repositories.ProblemRepository;
import com.careercrack.careercrack.models.Problem;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Service
public class ProblemService {
    private final ProblemRepository problemRepository;

    public ProblemService(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    public Page<Problem> getAllProblems(Pageable pageable) {
        return problemRepository.findAll(pageable);
    }

    public Optional<Problem> findById(Long id) {
        return problemRepository.findById(id);
    }

    public Problem createUser(Problem problem) {
        return problemRepository.save(problem);
    }

    public Problem updateProblem(Long id, Problem problem) {
        Problem existingProblem = findById(id).orElse(null);
        if(existingProblem != null) {
            existingProblem.setTitle(problem.getTitle());
            existingProblem.setExternalLink(problem.getExternalLink());
            existingProblem.setDifficulty(problem.getDifficulty());
            existingProblem.setStatus(problem.getStatus());
            existingProblem.setDescription(problem.getDescription());
            existingProblem.setSolution(problem.getSolution());
            return existingProblem;
        }
        return null;
    }

    public void deleteProblem(Long id) {
        problemRepository.deleteById(id);
    }
}
