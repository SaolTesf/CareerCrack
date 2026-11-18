package com.careercrack.careercrack.dtos;

import com.careercrack.careercrack.enums.ApplicationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class StatusHistoryResponse {
    private Long id;
    // null on the first row, when the application was created
    private ApplicationStatus fromStatus;
    private ApplicationStatus toStatus;
    private LocalDateTime changedAt;
}
