package com.careercrack.careercrack.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A company belongs to one user. Names are unique per user ignoring case (a database index enforces it),
 * so look companies up with a case-insensitive query before creating one.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "companies", schema = "careercrack")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @NotBlank(message = "Company name is required")
    @Size(max = 255, message = "Company name must be at most 255 characters")
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    // only http(s) links, so a saved link can never be a javascript: URL
    @Size(max = 2048, message = "Website must be at most 2048 characters")
    @Pattern(regexp = "^https?://\\S+$", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Website must start with http:// or https://")
    @Column(name = "website", columnDefinition = "TEXT")
    private String website;

    @Size(max = 100, message = "Industry must be at most 100 characters")
    @Column(name = "industry", length = 100)
    private String industry;

    @Size(max = 255, message = "Location must be at most 255 characters")
    @Column(name = "location", length = 255)
    private String location;

    @Size(max = 10000, message = "Notes must be at most 10000 characters")
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // contacts go with the company (the database does the same with ON DELETE CASCADE).
    // Deleting a company that still has applications fails on purpose: see applications.company_id.
    @OneToMany(mappedBy = "company", cascade = CascadeType.REMOVE)
    private Set<Contact> contacts = new LinkedHashSet<>();
}
