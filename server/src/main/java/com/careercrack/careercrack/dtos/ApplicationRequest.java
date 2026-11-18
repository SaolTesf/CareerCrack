package com.careercrack.careercrack.dtos;

import com.careercrack.careercrack.enums.ApplicationStatus;
import com.careercrack.careercrack.enums.SalaryPeriod;
import com.careercrack.careercrack.enums.WorkMode;
import com.careercrack.careercrack.validation.CrossFieldValidated;
import com.careercrack.careercrack.validation.ValidCrossFields;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Body for both creating (POST) and editing (PUT) an application.
 * An edit replaces every field here, so send the whole form: a field sent as null is cleared.
 * The exceptions are status, salaryCurrency and salaryPeriod, where null means "use the default / leave as is".
 */
@Getter
@Setter
@NoArgsConstructor
@ValidCrossFields
public class ApplicationRequest implements CrossFieldValidated {

    // Send companyId for a company the user already has, or companyName to find or create one.
    // If both are sent, companyId wins.
    private Long companyId;

    @Size(max = 255, message = "Company name must be at most 255 characters")
    private String companyName;

    @NotBlank(message = "Role title is required")
    @Size(max = 255, message = "Role title must be at most 255 characters")
    private String roleTitle;

    // null: BOOKMARKED when creating, unchanged when editing. A change is recorded in the status history.
    private ApplicationStatus status;

    @DecimalMin(value = "0.00", message = "Minimum salary cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Minimum salary must have at most 10 digits and 2 decimals")
    private BigDecimal salaryMin;

    @DecimalMin(value = "0.00", message = "Maximum salary cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Maximum salary must have at most 10 digits and 2 decimals")
    private BigDecimal salaryMax;

    // either case is accepted; the service saves it upper case. null means USD.
    @Pattern(regexp = "^[A-Za-z]{3}$", message = "Currency must be a 3-letter code such as USD")
    private String salaryCurrency;

    // null means YEARLY
    private SalaryPeriod salaryPeriod;

    @Size(max = 255, message = "Location must be at most 255 characters")
    private String location;

    private WorkMode workMode;

    @Size(max = 2048, message = "Job link must be at most 2048 characters")
    @Pattern(regexp = "^https?://\\S+$", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Job link must start with http:// or https://")
    private String jobUrl;

    @Size(max = 50000, message = "Job description must be at most 50000 characters")
    private String jobDescription;

    private LocalDate postingDate;

    private LocalDate appliedDate;

    @Size(max = 100, message = "Source must be at most 100 characters")
    private String source;

    @Size(max = 10000, message = "Notes must be at most 10000 characters")
    private String notes;

    @Override
    public Map<String, String> crossFieldErrors() {
        Map<String, String> errors = new LinkedHashMap<>();
        if (companyId == null && (companyName == null || companyName.isBlank())) {
            errors.put("companyName", "Company is required");
        }
        if (salaryMin != null && salaryMax != null && salaryMax.compareTo(salaryMin) < 0) {
            errors.put("salaryMax", "Maximum salary must be at least the minimum salary");
        }
        return errors;
    }
}
