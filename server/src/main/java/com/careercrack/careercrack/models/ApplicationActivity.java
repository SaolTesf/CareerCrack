package com.careercrack.careercrack.models;

import com.careercrack.careercrack.enums.ActivityType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;

/** A note in the application's log: interview feedback, questions asked, calls, emails. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "application_activities", schema = "careercrack")
public class ApplicationActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull(message = "Application is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false, updatable = false)
    private JobApplication application;

    // optional: the scheduled interview this note is about (must belong to the same application)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private ApplicationEvent event;

    @NotNull(message = "Activity type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false, length = 20)
    private ActivityType activityType = ActivityType.NOTE;

    // e.g. "Technical round 1"
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be at most 255 characters")
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @NotNull(message = "Date and time are required")
    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt = Instant.now();

    @Size(max = 20000, message = "Questions must be at most 20000 characters")
    @Column(name = "questions_asked", columnDefinition = "TEXT")
    private String questionsAsked;

    // feedback and thoughts
    @Size(max = 20000, message = "Notes must be at most 20000 characters")
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // how it felt, 1 (poorly) to 5 (great)
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    @Column(name = "self_rating")
    private Short selfRating;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
