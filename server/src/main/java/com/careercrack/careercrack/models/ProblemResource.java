package com.careercrack.careercrack.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "problem_resources", schema = "careercrack")
public class ProblemResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, updatable = false)
    private Long id;

    @Column(name = "problem_id")
    @NotNull
    private Long problemId;

    @Column(name = "resource_type")
    @Size(max = 50)
    private String resourceType;

    @Column(name = "url")
    @NotNull
    private String url;

    @Column(name = "description")
    private String description;
}
