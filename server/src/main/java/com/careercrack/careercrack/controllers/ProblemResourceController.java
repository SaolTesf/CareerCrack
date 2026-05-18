package com.careercrack.careercrack.controllers;

import com.careercrack.careercrack.models.ProblemResource;
import com.careercrack.careercrack.services.ProblemResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/problem-resources")
public class ProblemResourceController {
    private final ProblemResourceService problemResourceService;

    public ProblemResourceController(ProblemResourceService problemResourceService) {
        this.problemResourceService = problemResourceService;
    }

    @PostMapping
    public ResponseEntity<ProblemResource> createProblemResource(ProblemResource problemResource) {
        ProblemResource newProblemResource = problemResourceService.createProblemResource(problemResource);
        log.info("Created problem resource for problem with ID {}", newProblemResource.getProblemId());
        return new ResponseEntity<>(newProblemResource, HttpStatus.CREATED);
    }
}
