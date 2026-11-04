package com.careercrack.careercrack.models;

import com.careercrack.careercrack.enums.ImportFormat;
import com.careercrack.careercrack.enums.ImportStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/** One CSV/Excel import. Applications it creates point back here so the import can be reviewed or undone. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "import_jobs", schema = "careercrack")
public class ImportJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @NotBlank(message = "File name is required")
    @Size(max = 255, message = "File name must be at most 255 characters")
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @NotNull(message = "File format is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "file_format", nullable = false, length = 10)
    private ImportFormat fileFormat;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ImportStatus status = ImportStatus.PENDING;

    @NotNull
    @Min(value = 0, message = "Total rows cannot be negative")
    @Column(name = "total_rows", nullable = false)
    private Integer totalRows = 0;

    @NotNull
    @Min(value = 0, message = "Imported rows cannot be negative")
    @Column(name = "imported_rows", nullable = false)
    private Integer importedRows = 0;

    @NotNull
    @Min(value = 0, message = "Failed rows cannot be negative")
    @Column(name = "failed_rows", nullable = false)
    private Integer failedRows = 0;

    // row-level problems, e.g. "row 14: missing role title"
    @Column(name = "error_report", columnDefinition = "TEXT")
    private String errorReport;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
