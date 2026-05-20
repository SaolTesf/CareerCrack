package com.careercrack.careercrack.models;


import com.careercrack.careercrack.enums.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "problem_categories", schema = "careercrack")
public class ProblemCategory {

    @Id
    @Column(name = "id", unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", unique = true)
    @NotNull
    @Size(min = 1, max = 50)
    private Category name;

    @Column(name = "description")
    private String description;
}
