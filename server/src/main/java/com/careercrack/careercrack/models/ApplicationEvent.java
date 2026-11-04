package com.careercrack.careercrack.models;

import com.careercrack.careercrack.enums.EventType;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
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

/** A follow-up, interview, assessment or offer deadline. Times are instants because interviews cross time zones. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "application_events", schema = "careercrack")
public class ApplicationEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull(message = "Application is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false, updatable = false)
    private JobApplication application;

    @NotNull(message = "Event type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 20)
    private EventType eventType;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be at most 255 characters")
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @NotNull(message = "Date and time are required")
    @Column(name = "event_at", nullable = false)
    private Instant eventAt;

    // address or meeting link
    @Size(max = 2000, message = "Location must be at most 2000 characters")
    @Column(name = "location", columnDefinition = "TEXT")
    private String location;

    // when to remind the user; reminderSentAt lets a scheduled job skip reminders it already sent
    @Column(name = "remind_at")
    private Instant remindAt;

    @Column(name = "reminder_sent_at")
    private Instant reminderSentAt;

    @Column(name = "completed", nullable = false)
    private boolean completed = false;

    @Size(max = 10000, message = "Notes must be at most 10000 characters")
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @AssertTrue(message = "The reminder must be at or before the event")
    public boolean isReminderNotAfterEvent() {
        return remindAt == null || eventAt == null || !remindAt.isAfter(eventAt);
    }
}
