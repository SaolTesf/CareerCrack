package com.careercrack.careercrack.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;

@Entity
public class Tag {
    @Id
    @Column(name = "id", unique = true, updatable = false)
    private Long id;

    @Column(name = "name")
    @Size(max = 50)
    private String name;
}
