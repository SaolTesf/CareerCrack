package com.careercrack.careercrack.dtos;

import com.careercrack.careercrack.enums.EventType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

/** applicationId, roleTitle and companyName are included so a calendar or dashboard can show where each event belongs. */
@Getter
@Setter
@NoArgsConstructor
public class EventResponse {
    private Long id;
    private Long applicationId;
    private String roleTitle;
    private String companyName;
    private EventType eventType;
    private String title;
    private Instant eventAt;
    private String location;
    private Instant remindAt;
    private boolean completed;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
