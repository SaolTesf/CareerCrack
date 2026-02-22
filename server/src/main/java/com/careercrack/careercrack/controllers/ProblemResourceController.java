package com.careercrack.careercrack.controllers;

import com.careercrack.careercrack.repositories.ProblemResourceRepository;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProblemResourceController {
    private final ProblemResourceRepository problemResourceRepository;

    public ProblemResourceController(ProblemResourceRepository problemResourceRepository) {
        this.problemResourceRepository = problemResourceRepository;
    }
}
