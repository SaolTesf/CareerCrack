package com.careercrack.careercrack.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The raw text of one row from an uploaded CSV or Excel file, before anything is checked. Everything is a String
 * so a bad cell ("Offer Recieved", "13/45/2026") can be reported with its row number instead of failing the whole file.
 * The import service turns each row into an ApplicationRequest and validates that.
 * Export writes these same columns, so an exported file can be imported again.
 */
@Getter
@Setter
@NoArgsConstructor
public class ApplicationImportRow {

    // the row's line in the file (the header is row 1), for error messages
    private int rowNumber;

    private String companyName;
    private String roleTitle;
    private String status;
    private String salaryMin;
    private String salaryMax;
    private String salaryCurrency;
    private String salaryPeriod;
    private String location;
    private String workMode;
    private String jobUrl;
    private String postingDate;
    private String appliedDate;
    private String source;
    private String notes;
}
