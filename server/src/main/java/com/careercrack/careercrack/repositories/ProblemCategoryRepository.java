package com.careercrack.careercrack.repositories;


import com.careercrack.careercrack.enums.Category;
import com.careercrack.careercrack.models.Problem;
import com.careercrack.careercrack.models.ProblemCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProblemCategoryRepository extends JpaRepository<ProblemCategory, Long> {

    @Query("SELECT p FROM Problem p JOIN ProblemCategory pc on p.problemCategory.id = pc.id WHERE pc.name = :name")
    Page<Problem> findAllProblemsByName(@Param("name") String name, Pageable pageable);

    Optional<ProblemCategory> findProblemCategoryByName(Category name);
}