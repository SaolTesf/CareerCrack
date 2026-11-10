package com.careercrack.careercrack.repositories;

import com.careercrack.careercrack.models.ApplicationEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Events have no user column, so ownership is checked through the application they belong to. */
public interface ApplicationEventRepository extends JpaRepository<ApplicationEvent, Long> {

    Optional<ApplicationEvent> findByIdAndApplication_User_Id(Long id, Long userId);

    // for routes like /applications/{applicationId}/events/{id}
    Optional<ApplicationEvent> findByIdAndApplication_IdAndApplication_User_Id(Long id, Long applicationId, Long userId);

    @Query("""
            select e from ApplicationEvent e
            where e.application.id = :applicationId and e.application.user.id = :userId
            order by e.eventAt
            """)
    List<ApplicationEvent> findAllForApplication(@Param("applicationId") Long applicationId, @Param("userId") Long userId);

    // ---- dashboard and calendar: application and company come back in the same query ----

    // unfinished events inside a window, soonest first
    @Query("""
            select e from ApplicationEvent e
            join fetch e.application a
            join fetch a.company
            where a.user.id = :userId and e.completed = false
              and e.eventAt >= :start and e.eventAt < :end
            order by e.eventAt
            """)
    List<ApplicationEvent> findUpcoming(@Param("userId") Long userId, @Param("start") Instant start, @Param("end") Instant end);

    // unfinished events that are already in the past
    @Query("""
            select e from ApplicationEvent e
            join fetch e.application a
            join fetch a.company
            where a.user.id = :userId and e.completed = false and e.eventAt < :now
            order by e.eventAt
            """)
    List<ApplicationEvent> findOverdue(@Param("userId") Long userId, @Param("now") Instant now);

    // everything in a window, finished or not, for a calendar view
    @Query("""
            select e from ApplicationEvent e
            join fetch e.application a
            join fetch a.company
            where a.user.id = :userId and e.eventAt >= :start and e.eventAt < :end
            order by e.eventAt
            """)
    List<ApplicationEvent> findInRange(@Param("userId") Long userId, @Param("start") Instant start, @Param("end") Instant end);

    // ---- reminder job (looks at every user, so it takes no user id) ----

    // Reminders that are due and not sent yet. Pass a PageRequest to work through them in batches.
    @Query("""
            select e from ApplicationEvent e
            join fetch e.application a
            join fetch a.user
            join fetch a.company
            where e.remindAt is not null and e.remindAt <= :now
              and e.reminderSentAt is null and e.completed = false
            order by e.remindAt
            """)
    List<ApplicationEvent> findDueReminders(@Param("now") Instant now, Pageable pageable);

    // Claims a reminder before sending it. Returns 1 if this caller got it and 0 if another instance already did,
    // so two running copies of the app never send the same reminder twice.
    @Modifying
    @Query("update ApplicationEvent e set e.reminderSentAt = :sentAt where e.id = :id and e.reminderSentAt is null")
    int markReminderSent(@Param("id") Long id, @Param("sentAt") Instant sentAt);
}
