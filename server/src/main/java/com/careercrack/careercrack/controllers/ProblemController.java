package com.careercrack.careercrack.controllers;

import com.careercrack.careercrack.dtos.CreateProblemRequest;
import com.careercrack.careercrack.dtos.UpdateProblemRequest;
import com.careercrack.careercrack.models.Problem;
import com.careercrack.careercrack.services.ProblemService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/problems")
public class ProblemController {
    private final ProblemService problemService;

    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    // Source: https://www.geeksforgeeks.org/advance-java/pagination-and-sorting-with-spring-data-jpa/
    @GetMapping
    public ResponseEntity<Page<Problem>> getAllProblems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        log.info("Retrieving {} problems for page {}, sorting by {} and ascending {}", size, page, sortBy, ascending);
        return ResponseEntity.ok(problemService.getAllProblems(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Problem> getProblemById(@PathVariable Long id) {
        Optional<Problem> problem = problemService.findById(id);
        log.info("Retrieving problem with id {}", id);
        return problem.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Problem> createProblem(@Valid @RequestBody CreateProblemRequest createProblemRequest) {
        Problem newProblem = problemService.createProblem(createProblemRequest);
        log.info("Created problem with id {}", newProblem.getId());
        return new ResponseEntity<>(newProblem, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Problem> updateProblem(@PathVariable Long id, @Valid @RequestBody UpdateProblemRequest updateProblemRequest) {
        Problem updatedProblem = problemService.updateProblem(id, updateProblemRequest);
        log.info("Updated Problem with ID {}", updatedProblem.getId());
        return ResponseEntity.ok(updatedProblem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProblem(@PathVariable Long id) {
        boolean deleted = problemService.deleteProblem(id);
        if(deleted) {
            log.info("Deleted Problem with ID {}", id);
            return ResponseEntity.noContent().build();
        }
        else {
            log.error("Failed to delete Problem with ID {}", id);
            return ResponseEntity.badRequest().build();
        }
    }
}
