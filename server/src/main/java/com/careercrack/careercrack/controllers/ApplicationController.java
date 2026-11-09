package com.careercrack.careercrack.controllers;

import com.careercrack.careercrack.dtos.ApplicationContactsRequest;
import com.careercrack.careercrack.dtos.ApplicationRequest;
import com.careercrack.careercrack.dtos.ApplicationResponse;
import com.careercrack.careercrack.dtos.ApplicationSummaryResponse;
import com.careercrack.careercrack.dtos.BoardColumnResponse;
import com.careercrack.careercrack.dtos.ContactResponse;
import com.careercrack.careercrack.dtos.PageResponse;
import com.careercrack.careercrack.dtos.StatusHistoryResponse;
import com.careercrack.careercrack.dtos.UpdateApplicationStatusRequest;
import com.careercrack.careercrack.enums.ApplicationStatus;
import com.careercrack.careercrack.services.ApplicationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<ApplicationSummaryResponse>> list(
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appliedFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appliedTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "false") boolean ascending) {
        log.info("Listing applications");
        return ResponseEntity.ok(applicationService.list(status, search, companyId, appliedFrom, appliedTo,
                page, size, sortBy, ascending));
    }

    // every stage in pipeline order, each with its cards
    @GetMapping("/board")
    public ResponseEntity<List<BoardColumnResponse>> board() {
        return ResponseEntity.ok(applicationService.board());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.get(id));
    }

    @PostMapping
    public ResponseEntity<ApplicationResponse> create(@Valid @RequestBody ApplicationRequest request) {
        log.info("Creating application");
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApplicationResponse> update(@PathVariable Long id, @Valid @RequestBody ApplicationRequest request) {
        log.info("Updating application {}", id);
        return ResponseEntity.ok(applicationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Deleting application {}", id);
        applicationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // a Kanban drop: returns the column the card landed in
    @PatchMapping("/{id}/status")
    public ResponseEntity<BoardColumnResponse> move(@PathVariable Long id, @Valid @RequestBody UpdateApplicationStatusRequest request) {
        log.info("Moving application {} to {}", id, request.getStatus());
        return ResponseEntity.ok(applicationService.move(id, request));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<StatusHistoryResponse>> history(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.history(id));
    }

    // replaces the linked contacts with exactly the ones listed
    @PutMapping("/{id}/contacts")
    public ResponseEntity<List<ContactResponse>> setContacts(@PathVariable Long id, @Valid @RequestBody ApplicationContactsRequest request) {
        log.info("Setting contacts for application {}", id);
        return ResponseEntity.ok(applicationService.setContacts(id, request));
    }
}
