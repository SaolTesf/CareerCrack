package com.careercrack.careercrack.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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

/** A recruiter, hiring manager or interviewer at a company. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "contacts", schema = "careercrack")
public class Contact {
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

    @NotBlank(message = "Contact name is required")
    @Size(max = 255, message = "Contact name must be at most 255 characters")
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    // free text: Recruiter, Hiring Manager, Interviewer, ...
    @Size(max = 100, message = "Role must be at most 100 characters")
    @Column(name = "contact_role", length = 100)
    private String contactRole;

    @Email(message = "Please provide a valid email address")
    @Size(max = 255, message = "Email must be at most 255 characters")
    @Column(name = "email", length = 255)
    private String email;

    @Size(max = 50, message = "Phone must be at most 50 characters")
    @Column(name = "phone", length = 50)
    private String phone;

    @Size(max = 2048, message = "LinkedIn URL must be at most 2048 characters")
    @Pattern(regexp = "^https?://\\S+$", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "LinkedIn URL must start with http:// or https://")
    @Column(name = "linkedin_url", columnDefinition = "TEXT")
    private String linkedinUrl;

    @Size(max = 10000, message = "Notes must be at most 10000 characters")
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // contacts sit in a Set on JobApplication, so equality is by id (two unsaved contacts are never equal)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contact other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Contact.class.hashCode();
    }
}
