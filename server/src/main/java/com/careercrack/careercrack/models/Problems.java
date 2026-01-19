package com.careercrack.careercrack.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

public class Problems {

    public enum Status {
        TODO,
        ATTEMPTED,
        SOLVED,
        REVIEW,
        MASTERED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, updatable = false)
    private Long id;

    @Column(name = "user_id")
    @NotNull
    private Long userId;

    @Column(name = "category_id")
    @NotNull
    private Long categoryId;

    @Column(name = "title")
    @NotNull
    @Size(min = 5, max = 255)
    private String title;

    @Column(name = "external_link")
    private String externalLink;

    @Column(name = "difficulty")
    @Size(max = 10)
    private String difficulty;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Size(min = 4, max = 20)
    private Status status;

    @Column(name = "description")
    private String description;

    @Column(name = "solution")
    private String solution;

    @Column(name = "created_at")
    @CreationTimestamp
    private Date created_at;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Date updated_at;
}
