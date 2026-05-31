package com.careercrack.careercrack.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProblemResourceResponse {
    private Long id;
    private Long problemId;
    private String resourceType;
    private String url;
    private String description;
}
