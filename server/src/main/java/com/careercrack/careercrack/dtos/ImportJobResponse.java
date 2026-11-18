package com.careercrack.careercrack.dtos;

import com.careercrack.careercrack.enums.ImportFormat;
import com.careercrack.careercrack.enums.ImportStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ImportJobResponse {
    private Long id;
    private String fileName;
    private ImportFormat fileFormat;
    private ImportStatus status;
    private Integer totalRows;
    private Integer importedRows;
    private Integer failedRows;
    // one problem per line, e.g. "Row 14: role title is required"
    private String errorReport;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
