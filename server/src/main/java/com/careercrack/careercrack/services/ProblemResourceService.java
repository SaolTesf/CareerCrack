package com.careercrack.careercrack.services;

import com.careercrack.careercrack.models.ProblemResource;
import com.careercrack.careercrack.repositories.ProblemResourceRepository;
import org.springframework.stereotype.Service;

@Service
public class ProblemResourceService {
    private final ProblemResourceRepository problemResourceRepository;

    public ProblemResourceService(ProblemResourceRepository problemResourceRepository) {
        this.problemResourceRepository = problemResourceRepository;
    }
}
