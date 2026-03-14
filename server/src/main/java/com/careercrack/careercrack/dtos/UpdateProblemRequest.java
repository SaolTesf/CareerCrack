package com.careercrack.careercrack.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateProblemRequest {

    @Size(min = 5, max = 255)
    private String title;

    private String externalLink;

    @Size(max = 10)
    private String difficulty;

    @Size(min = 4, max = 20)
    private String status;

    private String description;

    private String solution;

    private List<String> tags;
}
