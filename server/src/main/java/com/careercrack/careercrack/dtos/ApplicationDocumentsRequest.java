package com.careercrack.careercrack.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/** The full list of document versions attached to an application. Anything not listed is detached. */
@Getter
@Setter
@NoArgsConstructor
public class ApplicationDocumentsRequest {

    @NotNull(message = "Document ids are required")
    @Size(max = 20, message = "An application can have at most 20 documents")
    private Set<@NotNull(message = "Document id cannot be empty") Long> documentIds;
}
