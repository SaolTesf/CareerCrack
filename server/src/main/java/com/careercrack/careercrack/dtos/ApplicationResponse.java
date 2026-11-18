package com.careercrack.careercrack.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * The detail page: everything in the summary plus the long text and the related records.
 * Notes from interviews (activities) can be long, so they are fetched separately, one page at a time.
 */
@Getter
@Setter
@NoArgsConstructor
public class ApplicationResponse extends ApplicationSummaryResponse {
    private String jobDescription;
    private String notes;
    // the import this application came from, if any
    private Long importJobId;
    private CompanyResponse company;
    private List<ContactResponse> contacts = new ArrayList<>();
    private List<DocumentResponse> documents = new ArrayList<>();
    private List<StatusHistoryResponse> statusHistory = new ArrayList<>();
    private List<EventResponse> events = new ArrayList<>();
}
