package com.careercrack.careercrack.controllers;

import com.careercrack.careercrack.dtos.CompanyRequest;
import com.careercrack.careercrack.dtos.CompanyResponse;
import com.careercrack.careercrack.dtos.PageResponse;
import com.careercrack.careercrack.services.CompanyService;
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
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    // sortBy: name, industry, location, createdAt or updatedAt
    @GetMapping
    public ResponseEntity<PageResponse<CompanyResponse>> list(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "20") int size,
                                                              @RequestParam(defaultValue = "name") String sortBy,
                                                              @RequestParam(defaultValue = "true") boolean ascending) {
        log.info("Listing companies");
        return ResponseEntity.ok(companyService.list(page, size, sortBy, ascending));
    }

    // autocomplete: GET /api/companies/search?q=acm
    @GetMapping("/search")
    public ResponseEntity<List<CompanyResponse>> search(@RequestParam(defaultValue = "") String q) {
        return ResponseEntity.ok(companyService.search(q));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.get(id));
    }

    @PostMapping
    public ResponseEntity<CompanyResponse> create(@Valid @RequestBody CompanyRequest request) {
        log.info("Creating company");
        return ResponseEntity.status(HttpStatus.CREATED).body(companyService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponse> update(@PathVariable Long id, @Valid @RequestBody CompanyRequest request) {
        log.info("Updating company {}", id);
        return ResponseEntity.ok(companyService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Deleting company {}", id);
        companyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
