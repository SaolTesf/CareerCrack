package com.careercrack.careercrack.services;


import com.careercrack.careercrack.dtos.CreateProblemRequest;
import com.careercrack.careercrack.models.Tag;
import com.careercrack.careercrack.repositories.ProblemRepository;
import com.careercrack.careercrack.models.Problem;
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

    public ProblemService(ProblemRepository problemRepository, TagService tagService, UserService userService, ProblemCategoryService problemCategoryService) {
        this.problemRepository = problemRepository;
        this.tagService = tagService;
        this.userService = userService;
        this.problemCategoryService = problemCategoryService;
    }

    public Page<Problem> getAllProblems(Pageable pageable) {
        return problemRepository.findAll(pageable);
    }

    public Optional<Problem> findById(Long id) {
        return problemRepository.findById(id);
    }

    public Problem createProblem(CreateProblemRequest createProblemRequest) {
        // create and set all attributes of problem
        Problem newProblem = new Problem();
        newProblem.setUser(userService.findById(createProblemRequest.getUserId()).orElseThrow(() -> new IllegalArgumentException("User not found")));
        newProblem.setProblemCategory(problemCategoryService.findById(createProblemRequest.getCategoryId()).orElseThrow(() -> new IllegalArgumentException("Problem Category not found")));
        newProblem.setTitle(createProblemRequest.getTitle());
        newProblem.setExternalLink(createProblemRequest.getExternalLink());
        newProblem.setDifficulty(createProblemRequest.getDifficulty());
        newProblem.setStatus(Problem.Status.valueOf(createProblemRequest.getStatus())); // Status attribute expects the Status enum type
        newProblem.setDescription(createProblemRequest.getDescription());
        newProblem.setSolution(createProblemRequest.getDescription());

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

        return savedProblem;
    }

    public Problem updateProblem(Long id, Problem problem) {
        Problem existingProblem = findById(id).orElse(null);
        if(existingProblem != null) {
            existingProblem.setTitle(problem.getTitle());
            existingProblem.setExternalLink(problem.getExternalLink());
            existingProblem.setDifficulty(problem.getDifficulty());
            existingProblem.setStatus(problem.getStatus());
            existingProblem.setDescription(problem.getDescription());
            existingProblem.setSolution(problem.getSolution());
            return existingProblem;
        }
        return null;
    }

    public void deleteProblem(Long id) {
        problemRepository.deleteById(id);
    }
}
