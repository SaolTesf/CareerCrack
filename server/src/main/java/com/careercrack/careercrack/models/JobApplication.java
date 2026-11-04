package com.careercrack.careercrack.models;

import com.careercrack.careercrack.enums.ApplicationStatus;
import com.careercrack.careercrack.enums.SalaryPeriod;
import com.careercrack.careercrack.enums.WorkMode;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * One job application (table "applications"; named JobApplication to avoid clashing with Spring's Application classes).
 *
 * Hibernate writes every column on insert, including nulls, so fields that have a database default
 * (status, salaryCurrency, salaryPeriod, sortOrder) are given the same default here.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "applications", schema = "careercrack")
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @NotNull(message = "Company is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // set when the row came from an import, so the whole import can be undone
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "import_job_id")
    private ImportJob importJob;

    @NotBlank(message = "Role title is required")
    @Size(max = 255, message = "Role title must be at most 255 characters")
    @Column(name = "role_title", nullable = false, length = 255)
    private String roleTitle;

    // For changes after creation use changeStatus(), which also writes the history row the dashboard relies on.
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ApplicationStatus status = ApplicationStatus.BOOKMARKED;

    @DecimalMin(value = "0.00", message = "Minimum salary cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Minimum salary must have at most 10 digits and 2 decimals")
    @Column(name = "salary_min", precision = 12, scale = 2)
    private BigDecimal salaryMin;

    @DecimalMin(value = "0.00", message = "Maximum salary cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Maximum salary must have at most 10 digits and 2 decimals")
    @Column(name = "salary_max", precision = 12, scale = 2)
    private BigDecimal salaryMax;

    // upper case ISO code (USD, EUR): normalise before saving
    @NotNull(message = "Currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a 3-letter code such as USD")
    @Column(name = "salary_currency", nullable = false, length = 3)
    private String salaryCurrency = "USD";

    @NotNull(message = "Salary period is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "salary_period", nullable = false, length = 10)
    private SalaryPeriod salaryPeriod = SalaryPeriod.YEARLY;

    @Size(max = 255, message = "Location must be at most 255 characters")
    @Column(name = "location", length = 255)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_mode", length = 10)
    private WorkMode workMode;

    // only http(s) links, so a saved link can never be a javascript: URL
    @Size(max = 2048, message = "Job link must be at most 2048 characters")
    @Pattern(regexp = "^https?://\\S+$", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Job link must start with http:// or https://")
    @Column(name = "job_url", columnDefinition = "TEXT")
    private String jobUrl;

    // a saved copy of the posting, because postings get taken down
    @Size(max = 50000, message = "Job description must be at most 50000 characters")
    @Column(name = "job_description", columnDefinition = "TEXT")
    private String jobDescription;

    @Column(name = "posting_date")
    private LocalDate postingDate;

    @Column(name = "applied_date")
    private LocalDate appliedDate;

    // where it was found: LinkedIn, referral, company site, ...
    @Size(max = 100, message = "Source must be at most 100 characters")
    @Column(name = "source", length = 100)
    private String source;

    @Size(max = 10000, message = "Notes must be at most 10000 characters")
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // position inside its Kanban column (lower = higher up)
    @NotNull
    @Min(value = 0, message = "Sort order cannot be negative")
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ---- relationships -------------------------------------------------------------------------

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("changedAt ASC, id ASC")
    private Set<ApplicationStatusHistory> statusHistory = new LinkedHashSet<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("eventAt ASC")
    private Set<ApplicationEvent> events = new LinkedHashSet<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("occurredAt DESC")
    private Set<ApplicationActivity> activities = new LinkedHashSet<>();

    // contacts and documents exist on their own; only the link rows are created and removed here
    @ManyToMany
    @JoinTable(name = "application_contacts", schema = "careercrack",
            joinColumns = @JoinColumn(name = "application_id"),
            inverseJoinColumns = @JoinColumn(name = "contact_id"))
    private Set<Contact> contacts = new LinkedHashSet<>();

    // attached_at is filled in by the database default
    @ManyToMany
    @JoinTable(name = "application_documents", schema = "careercrack",
            joinColumns = @JoinColumn(name = "application_id"),
            inverseJoinColumns = @JoinColumn(name = "document_id"))
    private Set<Document> documents = new LinkedHashSet<>();

    // ---- cross-field validation (mirrors the CHECK constraint in the database) --------------------

    @AssertTrue(message = "Maximum salary must be at least the minimum salary")
    public boolean isSalaryRangeValid() {
        return salaryMin == null || salaryMax == null || salaryMax.compareTo(salaryMin) >= 0;
    }

    // ---- helpers -------------------------------------------------------------------------------

    /** Call once when a new application is created, so its history starts with the first status. */
    public void recordInitialStatus() {
        if (statusHistory.isEmpty()) {
            statusHistory.add(new ApplicationStatusHistory(this, null, status));
        }
    }

    /** Moves the application to a new stage and records it. Sets the applied date the first time it reaches APPLIED. */
    public void changeStatus(ApplicationStatus newStatus) {
        if (newStatus == null || newStatus == status) {
            return;
        }
        statusHistory.add(new ApplicationStatusHistory(this, status, newStatus));
        status = newStatus;
        if (newStatus == ApplicationStatus.APPLIED && appliedDate == null) {
            appliedDate = LocalDate.now();
        }
    }

    public void addContact(Contact contact) {
        contacts.add(contact);
    }

    public void removeContact(Long contactId) {
        contacts.removeIf(c -> c.getId().equals(contactId));
    }

    public void attachDocument(Document document) {
        documents.add(document);
    }

    public void detachDocument(Long documentId) {
        documents.removeIf(d -> d.getId().equals(documentId));
    }
}
