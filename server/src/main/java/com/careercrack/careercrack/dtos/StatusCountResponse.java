package com.careercrack.careercrack.dtos;

import com.careercrack.careercrack.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StatusCountResponse {
    private final ApplicationStatus status;
    private final long count;
}
