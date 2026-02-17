package com.careercrack.careercrack.controllers;

import com.careercrack.careercrack.models.Problem;
import com.careercrack.careercrack.services.ProblemCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/problem-categories")
public class ProblemCategoryController {
    private final ProblemCategoryService problemCategoryService;

    public ProblemCategoryController(ProblemCategoryService problemCategoryService) {
        this.problemCategoryService = problemCategoryService;
    }

    @GetMapping()
    public ResponseEntity<Page<Problem>> getAllProblems(
            @RequestParam(required = true) String categoryName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        log.info("Retrieving {} problems in category {}, for page {}, sorting by {} and ascending {}", size, categoryName, page, sortBy, ascending);
        return ResponseEntity.ok(problemCategoryService.getAllProblems(categoryName, pageable));
    }
}
