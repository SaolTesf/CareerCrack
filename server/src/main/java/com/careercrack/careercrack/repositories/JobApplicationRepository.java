package com.careercrack.careercrack.repositories;

import com.careercrack.careercrack.enums.ApplicationStatus;
import com.careercrack.careercrack.models.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Every lookup takes the user's id, so one user can never read or change another user's applications.
 * For filtered, paged lists use findAll(JobApplicationSpecifications.filter(...), pageable).
 */
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long>, JpaSpecificationExecutor<JobApplication> {

    // ---- one application ----

    Optional<JobApplication> findByIdAndUser_Id(Long id, Long userId);

    // a cheap ownership check, for endpoints that only need a 404 for someone else's application
    boolean existsByIdAndUser_Id(Long id, Long userId);

    // company, contacts and documents in one query, for the detail page.
    // Events, activities and history have their own repositories so they can be ordered and paged.
    @EntityGraph(attributePaths = {"company", "contacts", "documents"})
    Optional<JobApplication> findWithDetailsByIdAndUser_Id(Long id, Long userId);

    // ---- lists (company comes back in the same query, so showing company names costs no extra queries) ----

    // the Kanban board: everything for the user. Sort by sortOrder and group by status on the client.
    @EntityGraph(attributePaths = "company")
    List<JobApplication> findAllByUser_Id(Long userId, Sort sort);

    // filtered and paged list; build the spec with JobApplicationSpecifications.filter(...)
    @Override
    @EntityGraph(attributePaths = "company")
    Page<JobApplication> findAll(Specification<JobApplication> spec, Pageable pageable);

    // one column of the Kanban board, top to bottom
    @EntityGraph(attributePaths = "company")
    List<JobApplication> findAllByUser_IdAndStatusOrderBySortOrderAscIdAsc(Long userId, ApplicationStatus status);

    // "recently updated" on the dashboard
    @EntityGraph(attributePaths = "company")
    List<JobApplication> findTop5ByUser_IdOrderByUpdatedAtDesc(Long userId);

    // the lowest free position in a column: -1 when the column is empty, so a new card gets this + 1
    @Query("select coalesce(max(a.sortOrder), -1) from JobApplication a where a.user.id = :userId and a.status = :status")
    Integer findMaxSortOrder(@Param("userId") Long userId, @Param("status") ApplicationStatus status);

    // ---- guards for the foreign keys that block deletes ----

    boolean existsByCompany_Id(Long companyId);

    boolean existsByContacts_Id(Long contactId);

    boolean existsByDocuments_Id(Long documentId);

    // duplicate check for imports: same company and role title for this user
    boolean existsByUser_IdAndCompany_IdAndRoleTitleIgnoreCase(Long userId, Long companyId, String roleTitle);

    // ---- dashboard ----

    // how many applications sit in each stage right now
    @Query("select a.status as status, count(a) as total from JobApplication a where a.user.id = :userId group by a.status")
    List<StatusCount> getStatusCounts(@Param("userId") Long userId);

    // applied dates since a day, for "applications per week" charts (group them in Java)
    @Query("""
            select a.appliedDate from JobApplication a
            where a.user.id = :userId and a.appliedDate >= :since
            order by a.appliedDate
            """)
    List<LocalDate> findAppliedDatesSince(@Param("userId") Long userId, @Param("since") LocalDate since);

    // sent vs interviewed vs offers, read from the application_funnel view. Empty if the user has no applications.
    @Query(value = """
            select total, applied, interviewed, offers, rejected, withdrawn
            from careercrack.application_funnel
            where user_id = :userId
            """, nativeQuery = true)
    Optional<FunnelRow> findFunnel(@Param("userId") Long userId);

    // ---- imports ----

    // Undoes an import. The database deletes the history, events, notes and links with the applications.
    // Companies the import created are left in place. Returns how many applications were deleted.
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from JobApplication a where a.importJob.id = :importJobId and a.user.id = :userId")
    int deleteAllByImportJob(@Param("importJobId") Long importJobId, @Param("userId") Long userId);

    // ---- result shapes for the dashboard queries ----

    interface StatusCount {
        ApplicationStatus getStatus();

        Long getTotal();
    }

    interface FunnelRow {
        Long getTotal();

        Long getApplied();

        Long getInterviewed();

        Long getOffers();

        Long getRejected();

        Long getWithdrawn();
    }
}