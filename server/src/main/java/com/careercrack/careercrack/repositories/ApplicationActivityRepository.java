package com.careercrack.careercrack.repositories;

import com.careercrack.careercrack.models.ApplicationActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** Activities have no user column, so ownership is checked through the application they belong to. */
public interface ApplicationActivityRepository extends JpaRepository<ApplicationActivity, Long> {

    @EntityGraph(attributePaths = "event")
    Optional<ApplicationActivity> findByIdAndApplication_User_Id(Long id, Long userId);

    // for routes like /applications/{applicationId}/activities/{id}
    @EntityGraph(attributePaths = "event")
    Optional<ApplicationActivity> findByIdAndApplication_IdAndApplication_User_Id(Long id, Long applicationId, Long userId);

    // pass a Pageable sorted by occurredAt descending to get the newest notes first
    @EntityGraph(attributePaths = "event")
    Page<ApplicationActivity> findAllByApplication_IdAndApplication_User_Id(Long applicationId, Long userId, Pageable pageable);
}
