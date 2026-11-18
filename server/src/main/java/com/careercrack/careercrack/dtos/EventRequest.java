package com.careercrack.careercrack.dtos;

import com.careercrack.careercrack.enums.EventType;
import com.careercrack.careercrack.validation.CrossFieldValidated;
import com.careercrack.careercrack.validation.ValidCrossFields;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;

/** Body for creating (POST) and editing (PUT) a follow-up, interview, assessment or deadline. */
@Getter
@Setter
@NoArgsConstructor
@ValidCrossFields
public class EventRequest implements CrossFieldValidated {

    @NotNull(message = "Event type is required")
    private EventType eventType;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be at most 255 characters")
    private String title;

    // an ISO instant such as 2026-11-03T19:00:00Z. Past dates are fine, so finished interviews can be logged.
    @NotNull(message = "Date and time are required")
    private Instant eventAt;

    // address or meeting link
    @Size(max = 2000, message = "Location must be at most 2000 characters")
    private String location;

    private Instant remindAt;

    // null: false when creating, unchanged when editing
    private Boolean completed;

    @Size(max = 10000, message = "Notes must be at most 10000 characters")
    private String notes;

    @Override
    public Map<String, String> crossFieldErrors() {
        if (remindAt != null && eventAt != null && remindAt.isAfter(eventAt)) {
            return Map.of("remindAt", "The reminder must be at or before the event");
        }
        return Map.of();
    }
}
