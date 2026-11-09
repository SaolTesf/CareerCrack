package com.careercrack.careercrack.controllers;

import com.careercrack.careercrack.dtos.ContactRequest;
import com.careercrack.careercrack.dtos.ContactResponse;
import com.careercrack.careercrack.dtos.PageResponse;
import com.careercrack.careercrack.services.ContactService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    // sortBy: name, contactRole, email, company.name, createdAt or updatedAt
    @GetMapping("/contacts")
    public ResponseEntity<PageResponse<ContactResponse>> list(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "20") int size,
                                                              @RequestParam(defaultValue = "name") String sortBy,
                                                              @RequestParam(defaultValue = "true") boolean ascending) {
        log.info("Listing contacts");
        return ResponseEntity.ok(contactService.list(page, size, sortBy, ascending));
    }

    @GetMapping("/companies/{companyId}/contacts")
    public ResponseEntity<List<ContactResponse>> listForCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(contactService.listForCompany(companyId));
    }

    @GetMapping("/contacts/{id}")
    public ResponseEntity<ContactResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(contactService.get(id));
    }

    @PostMapping("/contacts")
    public ResponseEntity<ContactResponse> create(@Valid @RequestBody ContactRequest request) {
        log.info("Creating contact");
        return ResponseEntity.status(HttpStatus.CREATED).body(contactService.create(request));
    }

    @PutMapping("/contacts/{id}")
    public ResponseEntity<ContactResponse> update(@PathVariable Long id, @Valid @RequestBody ContactRequest request) {
        log.info("Updating contact {}", id);
        return ResponseEntity.ok(contactService.update(id, request));
    }

    @DeleteMapping("/contacts/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Deleting contact {}", id);
        contactService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
