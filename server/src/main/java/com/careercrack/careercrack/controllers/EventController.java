package com.careercrack.careercrack.controllers;

import com.careercrack.careercrack.dtos.EventRequest;
import com.careercrack.careercrack.dtos.EventResponse;
import com.careercrack.careercrack.services.ApplicationEventService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class EventController {

    private final ApplicationEventService eventService;

    public EventController(ApplicationEventService eventService) {
        this.eventService = eventService;
    }

    // ---- across applications ----

    /**
     * The calendar: GET /api/events?from=2026-11-01T00:00:00Z&to=2026-12-01T00:00:00Z
     * With no dates it shows the next 30 days. The window can be at most 366 days.
     * includeCompleted=false leaves out events that are ticked off.
     */
    @GetMapping("/events")
    public ResponseEntity<List<EventResponse>> calendar(@RequestParam(required = false) Instant from,
                                                        @RequestParam(required = false) Instant to,
                                                        @RequestParam(defaultValue = "true") boolean includeCompleted) {
        return ResponseEntity.ok(eventService.calendar(from, to, includeCompleted));
    }

    // PATCH /api/events/12/complete ticks it off; add ?completed=false to untick it
    @PatchMapping("/events/{id}/complete")
    public ResponseEntity<EventResponse> complete(@PathVariable Long id, @RequestParam(defaultValue = "true") boolean completed) {
        log.info("Setting event {} completed={}", id, completed);
        return ResponseEntity.ok(eventService.setCompleted(id, completed));
    }

    // ---- events of one application ----

    @GetMapping("/applications/{applicationId}/events")
    public ResponseEntity<List<EventResponse>> list(@PathVariable Long applicationId) {
        return ResponseEntity.ok(eventService.listForApplication(applicationId));
    }

    @GetMapping("/applications/{applicationId}/events/{eventId}")
    public ResponseEntity<EventResponse> get(@PathVariable Long applicationId, @PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.get(applicationId, eventId));
    }

    @PostMapping("/applications/{applicationId}/events")
    public ResponseEntity<EventResponse> create(@PathVariable Long applicationId, @Valid @RequestBody EventRequest request) {
        log.info("Creating event for application {}", applicationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.create(applicationId, request));
    }

    @PutMapping("/applications/{applicationId}/events/{eventId}")
    public ResponseEntity<EventResponse> update(@PathVariable Long applicationId, @PathVariable Long eventId,
                                                @Valid @RequestBody EventRequest request) {
        log.info("Updating event {} of application {}", eventId, applicationId);
        return ResponseEntity.ok(eventService.update(applicationId, eventId, request));
    }

    @DeleteMapping("/applications/{applicationId}/events/{eventId}")
    public ResponseEntity<Void> delete(@PathVariable Long applicationId, @PathVariable Long eventId) {
        log.info("Deleting event {} of application {}", eventId, applicationId);
        eventService.delete(applicationId, eventId);
        return ResponseEntity.noContent().build();
    }
}
