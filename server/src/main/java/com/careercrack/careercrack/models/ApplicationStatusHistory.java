package com.careercrack.careercrack.models;

import com.careercrack.careercrack.enums.ApplicationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** One row each time an application changes stage. The dashboard counts conversions from these rows. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "application_status_history", schema = "careercrack")
public class ApplicationStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull(message = "Application is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false, updatable = false)
    private JobApplication application;

    // null on the first row, when the application is created
    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 30)
    private ApplicationStatus fromStatus;

    @NotNull(message = "New status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, length = 30)
    private ApplicationStatus toStatus;

    // set here instead of by the database so an import can record a past date
    @NotNull
    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt = LocalDateTime.now();

    public ApplicationStatusHistory(JobApplication application, ApplicationStatus fromStatus, ApplicationStatus toStatus) {
        this.application = application;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
    }
}
