package com.careercrack.careercrack.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProblemResponse {
    private Long id;
    private Long userId;
    private String categoryName;
    private String title;
    private String externalLink;
    private String difficulty;
    private String status;
    private String description;
    private String solution;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> tags;
}