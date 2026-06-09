package com.careercrack.careercrack.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateProblemResourceRequest {
    @NotNull
    private Long problemId;

    @Size(max = 50)
    private String resourceType;

    @NotNull
    private String url;

    @Column(name = "description")
    private String description;
}
