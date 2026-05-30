package com.careercrack.careercrack.services;


import com.careercrack.careercrack.dtos.CreateProblemRequest;
import com.careercrack.careercrack.dtos.ProblemResponse;
import com.careercrack.careercrack.dtos.UpdateProblemRequest;
import com.careercrack.careercrack.enums.Status;
import com.careercrack.careercrack.mappers.ProblemMapper;
import com.careercrack.careercrack.models.Tag;
import com.careercrack.careercrack.repositories.ProblemRepository;
import com.careercrack.careercrack.models.Problem;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class ProblemService {
    private final ProblemRepository problemRepository;
    private final TagService tagService;
    private final UserService userService;
    private final ProblemCategoryService problemCategoryService;
    private final ProblemMapper problemMapper;

    public ProblemService(ProblemRepository problemRepository, TagService tagService, UserService userService, ProblemCategoryService problemCategoryService, ProblemMapper problemMapper) {
        this.problemRepository = problemRepository;
        this.tagService = tagService;
        this.userService = userService;
        this.problemCategoryService = problemCategoryService;
        this.problemMapper = problemMapper;
    }

    public Page<ProblemResponse> getAllProblems(Pageable pageable) {
        Page<Problem> problems = problemRepository.findAll(pageable);
        return problems.map(problemMapper::toDto);
    }

    public Page<ProblemResponse> getAllProblemsByUserId(Long userId, Pageable pageable) {
        Page<Problem> problems = problemRepository.findAllByUserId(userId, pageable);
        return problems.map(problemMapper::toDto);
    }

    public Optional<ProblemResponse> findById(Long id) {
        return problemRepository.findById(id).map(problemMapper::toDto);
    }

    @Transactional
    public ProblemResponse createProblem(CreateProblemRequest createProblemRequest) {
        // create and set all attributes of problem
        Problem newProblem = new Problem();
        newProblem.setUser(userService.findById(createProblemRequest.getUserId()).orElseThrow(() -> new IllegalArgumentException("User not found")));
        newProblem.setProblemCategory(problemCategoryService.findById(createProblemRequest.getCategoryId()).orElseThrow(() -> new IllegalArgumentException("Problem Category not found")));
        newProblem.setTitle(createProblemRequest.getTitle());
        newProblem.setExternalLink(createProblemRequest.getExternalLink());
        newProblem.setDifficulty(createProblemRequest.getDifficulty());
        newProblem.setStatus(Status.valueOf(createProblemRequest.getStatus())); // Status attribute expects the Status enum type
        newProblem.setDescription(createProblemRequest.getDescription());
        newProblem.setSolution(createProblemRequest.getSolution());

        // save problem so ID can be created
        Problem savedProblem = problemRepository.save(newProblem);

        // either add or create tags (need the tag names in string format) if provided
        if(createProblemRequest.getTags() != null && !createProblemRequest.getTags().isEmpty()) {
            Set<Tag> problemTags = new HashSet<>();
            for(String tagName : createProblemRequest.getTags()) {
                Tag tag  = tagService.findOrCreate(tagName);
                problemTags.add(tag);
            }
            savedProblem.setTags(new ArrayList<>(problemTags));
            savedProblem = problemRepository.save(savedProblem);
        }

        return problemMapper.toDto(savedProblem);
    }

    @Transactional
    public ProblemResponse updateProblem(Long id, UpdateProblemRequest updateProblemRequest) {
        Problem existingProblem = problemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Problem not found"));

        if(updateProblemRequest.getCategory() != null) {
            existingProblem.setProblemCategory(problemCategoryService.findByName(updateProblemRequest.getCategory()).orElseThrow(() -> new IllegalArgumentException("Problem Category not found")));
        }

        if(updateProblemRequest.getTitle() != null) {
            existingProblem.setTitle(updateProblemRequest.getTitle());
        }
        if(updateProblemRequest.getExternalLink() != null) {
            existingProblem.setExternalLink(updateProblemRequest.getExternalLink());
        }
        if(updateProblemRequest.getDifficulty() != null) {
            existingProblem.setDifficulty(updateProblemRequest.getDifficulty());
        }
        if(updateProblemRequest.getStatus() != null) {
            existingProblem.setStatus(Status.valueOf(updateProblemRequest.getStatus()));
        }
        if(updateProblemRequest.getDescription() != null) {
            existingProblem.setDescription(updateProblemRequest.getDescription());
        }
        if(updateProblemRequest.getSolution() != null) {
            existingProblem.setSolution(updateProblemRequest.getSolution());
        }

        if(updateProblemRequest.getTags() != null && !updateProblemRequest.getTags().isEmpty()) {
            Set<Tag> updatedTag = new HashSet<>(existingProblem.getTags());
            for(String tag : updateProblemRequest.getTags()) {
                Tag newTag = tagService.findOrCreate(tag);
                updatedTag.add(newTag);
            }
            existingProblem.setTags(new ArrayList<>(updatedTag));
        }

        Problem savedProblem = problemRepository.save(existingProblem);
        return problemMapper.toDto(savedProblem);
    }

    public boolean deleteProblem(Long id) {
        if(!problemRepository.existsById(id)) {
            return false;
        }
        problemRepository.deleteById(id);
        return true;
    }
}
