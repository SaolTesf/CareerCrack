package com.careercrack.careercrack.services;

import com.careercrack.careercrack.dtos.CreateProblemResourceRequest;
import com.careercrack.careercrack.dtos.ProblemResourceResponse;
import com.careercrack.careercrack.mappers.ProblemResourceMapper;
import com.careercrack.careercrack.models.ProblemResource;
import com.careercrack.careercrack.repositories.ProblemResourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProblemResourceService {
    private final ProblemResourceRepository problemResourceRepository;
    private final ProblemResourceMapper problemResourceMapper;

    public ProblemResourceService(ProblemResourceRepository problemResourceRepository, ProblemResourceMapper problemResourceMapper) {
        this.problemResourceRepository = problemResourceRepository;
        this.problemResourceMapper = problemResourceMapper;
    }

    @Transactional
    public ProblemResourceResponse createProblemResource(CreateProblemResourceRequest createProblemResourceRequest) {
        ProblemResource newProblemResource = new ProblemResource();
        newProblemResource.setProblemId(createProblemResourceRequest.getProblemId());
        newProblemResource.setResourceType(createProblemResourceRequest.getResourceType());
        newProblemResource.setUrl(createProblemResourceRequest.getUrl());
        newProblemResource.setDescription(createProblemResourceRequest.getDescription());

        ProblemResource savedProblemResource = problemResourceRepository.save(newProblemResource);
        return problemResourceMapper.toDto(savedProblemResource);
    }

}
