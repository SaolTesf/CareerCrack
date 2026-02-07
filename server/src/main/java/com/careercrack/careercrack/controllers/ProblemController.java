package com.careercrack.careercrack.controllers;

import com.careercrack.careercrack.models.Problem;
import com.careercrack.careercrack.services.ProblemService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/problems")
public class ProblemController {
    private final ProblemService problemService;

    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    // Source: https://www.geeksforgeeks.org/advance-java/pagination-and-sorting-with-spring-data-jpa/
    @GetMapping
    public Page<Problem> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return problemService.getAllProblems(pageable);
    }

    @GetMapping("/{id}")
    public Optional<Problem> getProblemById(@PathVariable Long id) {
        return problemService.findById(id);
    }

    @PutMapping("/{id}")
    public Problem updateProblem(@PathVariable Long id, @Valid @RequestBody Problem problem) {
        return problemService.updateProblem(id, problem);
    }
}
