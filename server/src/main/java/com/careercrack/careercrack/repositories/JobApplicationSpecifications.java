package com.careercrack.careercrack.repositories;

import com.careercrack.careercrack.enums.ApplicationStatus;
import com.careercrack.careercrack.models.JobApplication;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Locale;

/**
 * Builds the list query for applications: any mix of filters, always limited to one user.
 * Filters that are null or blank are simply left out, so the caller can pass request parameters straight through.
 *
 * Usage: repository.findAll(JobApplicationSpecifications.filter(userId, status, search, null, null, null), pageable)
 */
public final class JobApplicationSpecifications {

    private JobApplicationSpecifications() {
    }

    public static Specification<JobApplication> filter(Long userId, ApplicationStatus status, String search,
                                                       Long companyId, LocalDate appliedFrom, LocalDate appliedTo) {
        Specification<JobApplication> spec = belongsTo(userId);
        if (status != null) {
            spec = spec.and(hasStatus(status));
        }
        if (companyId != null) {
            spec = spec.and(forCompany(companyId));
        }
        if (search != null && !search.isBlank()) {
            spec = spec.and(matchesSearch(search));
        }
        if (appliedFrom != null) {
            spec = spec.and(appliedOnOrAfter(appliedFrom));
        }
        if (appliedTo != null) {
            spec = spec.and(appliedOnOrBefore(appliedTo));
        }
        return spec;
    }

    public static Specification<JobApplication> belongsTo(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<JobApplication> hasStatus(ApplicationStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<JobApplication> forCompany(Long companyId) {
        return (root, query, cb) -> cb.equal(root.get("company").get("id"), companyId);
    }

    // role title, company name or location contains the text, ignoring case
    public static Specification<JobApplication> matchesSearch(String text) {
        String pattern = "%" + escapeLike(text.trim().toLowerCase(Locale.ROOT)) + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.<String>get("roleTitle")), pattern, '\\'),
                cb.like(cb.lower(root.get("company").<String>get("name")), pattern, '\\'),
                cb.like(cb.lower(root.<String>get("location")), pattern, '\\'));
    }

    public static Specification<JobApplication> appliedOnOrAfter(LocalDate date) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.<LocalDate>get("appliedDate"), date);
    }

    public static Specification<JobApplication> appliedOnOrBefore(LocalDate date) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.<LocalDate>get("appliedDate"), date);
    }

    // a typed % or _ must match itself, not act as a wildcard
    private static String escapeLike(String text) {
        return text.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
