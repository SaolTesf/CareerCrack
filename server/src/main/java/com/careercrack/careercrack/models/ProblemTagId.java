/* Since the ProblemTags table has a composite primary key, this composite key class is necessary */
package com.careercrack.careercrack.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProblemTagId implements Serializable { // will be serialized and sent across the network
    @Column(name = "problem_id", updatable = false)
    private Long problemId;

    @Column(name = "tag_id", updatable = false)
    private Long tagId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProblemTagId that)) return false;
        return Objects.equals(problemId, that.problemId) && Objects.equals(tagId, that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(problemId, tagId);
    }
}