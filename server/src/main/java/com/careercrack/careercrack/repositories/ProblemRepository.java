package com.careercrack.careercrack.repositories;

import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import com.careercrack.careercrack.models.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

    /*
        * The @EntityGraph annotation is used to specify which related entities should be fetched along with the main entity (Problem) when querying the database.
        * In this case, when fetching a Problem, it will also fetch the associated User, ProblemCategory, and Tags in a single query,
        * which can improve performance by reducing the number of queries needed to retrieve related data.
        * Overall fixing the N+1 select problem
    */

    @EntityGraph(attributePaths = {"user", "problemCategory", "tags"})
    @Nonnull
    Optional<Problem> findById(@Nonnull Long id);

    @EntityGraph(attributePaths = {"user", "problemCategory"})
    @Nonnull
    Page<Problem> findAll(@Nonnull Pageable pageable);

    @EntityGraph(attributePaths = {"user", "problemCategory"})
    Page<Problem> findAllByUserId(Long userId, Pageable pageable);
}
