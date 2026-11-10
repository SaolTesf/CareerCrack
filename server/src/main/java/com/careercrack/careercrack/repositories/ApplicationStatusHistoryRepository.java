package com.careercrack.careercrack.repositories;

import com.careercrack.careercrack.enums.ApplicationStatus;
import com.careercrack.careercrack.models.ApplicationStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * History rows are written through JobApplication.changeStatus(), never directly.
 * This repository only reads them: one application's timeline, and stage changes for the dashboard.
 */
public interface ApplicationStatusHistoryRepository extends JpaRepository<ApplicationStatusHistory, Long> {

    // oldest first
    @Query("""
            select h from ApplicationStatusHistory h
            where h.application.id = :applicationId and h.application.user.id = :userId
            order by h.changedAt, h.id
            """)
    List<ApplicationStatusHistory> findTimeline(@Param("applicationId") Long applicationId, @Param("userId") Long userId);

    // Every move into one of the given stages since a date, e.g. INTERVIEWING per week for a chart.
    @Query("""
            select h from ApplicationStatusHistory h
            where h.application.user.id = :userId and h.toStatus in :statuses and h.changedAt >= :since
            order by h.changedAt
            """)
    List<ApplicationStatusHistory> findTransitionsSince(@Param("userId") Long userId,
                                                        @Param("statuses") Collection<ApplicationStatus> statuses,
                                                        @Param("since") LocalDateTime since);
}
