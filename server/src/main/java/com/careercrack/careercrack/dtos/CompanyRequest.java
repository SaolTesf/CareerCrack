package com.careercrack.careercrack.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Body for creating (POST) and editing (PUT) a company. */
@Getter
@Setter
@NoArgsConstructor
public class CompanyRequest {

    @NotBlank(message = "Company name is required")
    @Size(max = 255, message = "Company name must be at most 255 characters")
    private String name;

    @Size(max = 2048, message = "Website must be at most 2048 characters")
    @Pattern(regexp = "^https?://\\S+$", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Website must start with http:// or https://")
    private String website;

    @Size(max = 100, message = "Industry must be at most 100 characters")
    private String industry;

    @Size(max = 255, message = "Location must be at most 255 characters")
    private String location;

    @Size(max = 10000, message = "Notes must be at most 10000 characters")
    private String notes;
}
