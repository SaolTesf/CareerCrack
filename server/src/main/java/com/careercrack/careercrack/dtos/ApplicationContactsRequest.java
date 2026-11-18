package com.careercrack.careercrack.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/** The full list of contacts linked to an application. Anything not listed is unlinked; an empty list unlinks all. */
@Getter
@Setter
@NoArgsConstructor
public class ApplicationContactsRequest {

    @NotNull(message = "Contact ids are required")
    @Size(max = 50, message = "An application can have at most 50 contacts")
    private Set<@NotNull(message = "Contact id cannot be empty") Long> contactIds;
}
