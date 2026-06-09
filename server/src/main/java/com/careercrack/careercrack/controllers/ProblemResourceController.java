package com.careercrack.careercrack.controllers;

import com.careercrack.careercrack.dtos.CreateProblemResourceRequest;
import com.careercrack.careercrack.dtos.ProblemResourceResponse;
import com.careercrack.careercrack.services.ProblemResourceService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public ResponseEntity<ProblemResourceResponse> createProblemResource(@Valid @RequestBody CreateProblemResourceRequest createProblemResourceRequest) {
        ProblemResourceResponse problemResourceResponse = problemResourceService.createProblemResource(createProblemResourceRequest);
        log.info("Created problem resource for problem with ID {}", problemResourceResponse.getProblemId());
        return new ResponseEntity<>(problemResourceResponse, HttpStatus.CREATED);
    }
}
