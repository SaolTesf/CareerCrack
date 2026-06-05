package com.careercrack.careercrack.controllers;

import com.careercrack.careercrack.dtos.TagResponse;
import com.careercrack.careercrack.services.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tags")
public class TagController {
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping()
    public ResponseEntity<List<TagResponse>> getAllTags() {
        log.info("Retrieving all tags");
        return ResponseEntity.ok(tagService.getAllTags());
    }
}
