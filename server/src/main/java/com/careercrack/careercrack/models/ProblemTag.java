package com.careercrack.careercrack.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "problem_tags", schema = "careercrack")
public class ProblemTag {

    @EmbeddedId
    private ProblemTagId problemTagId;

    // Many-to-one relationship to Tag
    // FetchType.LAZY to avoid loading the Tag unless needed
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("problemId")
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

}
