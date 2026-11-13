package com.careercrack.careercrack.dtos;

import com.careercrack.careercrack.enums.ActivityType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ActivityResponse {
    private Long id;
    private Long applicationId;
    // the scheduled interview this note is about, if any
    private Long eventId;
    private String eventTitle;
    private ActivityType activityType;
    private String title;
    private Instant occurredAt;
    private String questionsAsked;
    private String notes;
    private Short selfRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
