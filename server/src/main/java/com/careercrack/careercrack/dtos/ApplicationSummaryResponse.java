package com.careercrack.careercrack.dtos;

import com.careercrack.careercrack.enums.ApplicationStatus;
import com.careercrack.careercrack.enums.SalaryPeriod;
import com.careercrack.careercrack.enums.WorkMode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** What the list view and the Kanban cards need. The long fields and related records are in ApplicationResponse. */
@Getter
@Setter
@NoArgsConstructor
public class ApplicationSummaryResponse {
    private Long id;
    private Long companyId;
    private String companyName;
    private String roleTitle;
    private ApplicationStatus status;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String salaryCurrency;
    private SalaryPeriod salaryPeriod;
    private String location;
    private WorkMode workMode;
    private String jobUrl;
    private LocalDate postingDate;
    private LocalDate appliedDate;
    private String source;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
