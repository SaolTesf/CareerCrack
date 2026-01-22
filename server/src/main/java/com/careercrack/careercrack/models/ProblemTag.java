package com.careercrack.careercrack.models;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "problemTags", schema = "careercrack")
public class ProblemTag {

    @EmbeddedId
    private ProblemTagId problemTagId;

}
