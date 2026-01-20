package com.careercrack.careercrack.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "problem_resources", schema = "careercrack")
public class ProblemResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, updatable = false)
    private Long id;

    @Column(name = "problem_id")
    @NonNull
    private Long problemId;

    @Column(name = "resource_type")
    @Size(max = 50)
    private String resourceType;

    @Column(name = "url")
    private String url;

    @Column(name = "description")
    private String description;
}
