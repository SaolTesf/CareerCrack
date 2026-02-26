package com.careercrack.careercrack.controllers;

import com.careercrack.careercrack.models.ProblemResource;
import com.careercrack.careercrack.repositories.ProblemResourceRepository;
import com.careercrack.careercrack.services.ProblemResourceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProblemResourceController {
    private final ProblemResourceService problemResourceService;

    public ProblemResourceController(ProblemResourceService problemResourceService) {
        this.problemResourceService = problemResourceService;
    }

    @PostMapping
    public ResponseEntity<ProblemResource> createProblemResource(ProblemResource problemResource) {
        ProblemResource newProblemResource = problemResourceService.createProblemResource(problemResource);
        return new ResponseEntity<>(newProblemResource, HttpStatus.CREATED);
    }
}
