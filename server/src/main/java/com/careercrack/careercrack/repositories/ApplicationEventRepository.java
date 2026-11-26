package com.careercrack.careercrack.repositories;

import com.careercrack.careercrack.models.ApplicationEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Events have no user column, so ownership is checked through the application they belong to.
 * Every event response shows its application's role title and company name, so those come back in the same query.
 */
public interface ApplicationEventRepository extends JpaRepository<ApplicationEvent, Long> {

    @EntityGraph(attributePaths = {"application", "application.company"})
    Optional<ApplicationEvent> findByIdAndApplication_User_Id(Long id, Long userId);

    // for routes like /applications/{applicationId}/events/{id}
    @EntityGraph(attributePaths = {"application", "application.company"})
    Optional<ApplicationEvent> findByIdAndApplication_IdAndApplication_User_Id(Long id, Long applicationId, Long userId);

    @Query("""
            select e from ApplicationEvent e
            join fetch e.application a
            join fetch a.company
            where a.id = :applicationId and a.user.id = :userId
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

    // Reminders that are due and not sent yet: the reminder time has passed, but not before `earliest`
    // (a reminder that is many hours late is no longer useful, so it is left alone). Pass a PageRequest to work in batches.
    @Query("""
            select e from ApplicationEvent e
            join fetch e.application a
            join fetch a.user
            join fetch a.company
            where e.remindAt is not null and e.remindAt <= :now and e.remindAt >= :earliest
              and e.reminderSentAt is null and e.completed = false
            order by e.remindAt
            """)
    List<ApplicationEvent> findDueReminders(@Param("now") Instant now, @Param("earliest") Instant earliest, Pageable pageable);

    // Claims a reminder before it is sent. Returns 1 if this caller got it, and 0 if another copy of the app already did,
    // or the event was ticked off, or its reminder time changed since it was loaded. So a reminder is never sent twice.
    @Modifying
    @Query("""
            update ApplicationEvent e set e.reminderSentAt = :claimedAt
            where e.id = :id and e.reminderSentAt is null and e.completed = false and e.remindAt = :remindAt
            """)
    int claimReminder(@Param("id") Long id, @Param("remindAt") Instant remindAt, @Param("claimedAt") Instant claimedAt);

    // Gives a claim back after the send failed, so the next run tries again. It only releases the claim made at
    // claimedAt, so it cannot undo a newer claim made after the event was rescheduled.
    @Modifying
    @Query("update ApplicationEvent e set e.reminderSentAt = null where e.id = :id and e.reminderSentAt = :claimedAt")
    int releaseReminder(@Param("id") Long id, @Param("claimedAt") Instant claimedAt);
}
