package com.careercrack.careercrack.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Body for creating (POST) and editing (PUT) a contact. */
@Getter
@Setter
@NoArgsConstructor
public class ContactRequest {

    @NotNull(message = "Company is required")
    private Long companyId;

    @NotBlank(message = "Contact name is required")
    @Size(max = 255, message = "Contact name must be at most 255 characters")
    private String name;

    // free text: Recruiter, Hiring Manager, Interviewer, ...
    @Size(max = 100, message = "Role must be at most 100 characters")
    private String contactRole;

    @Email(message = "Please provide a valid email address")
    @Size(max = 255, message = "Email must be at most 255 characters")
    private String email;

    @Size(max = 50, message = "Phone must be at most 50 characters")
    private String phone;

    @Size(max = 2048, message = "LinkedIn URL must be at most 2048 characters")
    @Pattern(regexp = "^https?://\\S+$", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "LinkedIn URL must start with http:// or https://")
    private String linkedinUrl;

    @Size(max = 10000, message = "Notes must be at most 10000 characters")
    private String notes;
}
