package com.careercrack.careercrack.dtos;

import com.careercrack.careercrack.enums.ActivityType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/** Body for creating (POST) and editing (PUT) a note in an application's log. */
@Getter
@Setter
@NoArgsConstructor
public class ActivityRequest {

    // null means NOTE
    private ActivityType activityType;

    // e.g. "Technical round 1"
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be at most 255 characters")
    private String title;

    // null means now
    private Instant occurredAt;

    // optional: the scheduled interview this note is about (must belong to the same application)
    private Long eventId;

    @Size(max = 20000, message = "Questions must be at most 20000 characters")
    private String questionsAsked;

    // feedback and thoughts
    @Size(max = 20000, message = "Notes must be at most 20000 characters")
    private String notes;

    // how it felt, 1 (poorly) to 5 (great)
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Short selfRating;
}
