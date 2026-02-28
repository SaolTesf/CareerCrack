package com.careercrack.careercrack.repositories;

import com.careercrack.careercrack.models.Problem;
import com.careercrack.careercrack.models.ProblemTag;
import com.careercrack.careercrack.models.ProblemTagId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProblemTagRepository extends JpaRepository<ProblemTag, ProblemTagId> {
    Boolean existsByProblem_IdAndTag_Id(Long problemId, Long tagId);

    void deleteByProblem_Id(Long problemId);

    @Query("SELECT p FROM Problem p JOIN ProblemTag pt WHERE pt.tag.id = :tagId")
    Page<Problem> findAllProblemsById(@Param("tagId") Long id, Pageable pageable);
}
