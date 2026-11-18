package com.careercrack.careercrack.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Edits a document's label and notes. The file and its version number never change. */
@Getter
@Setter
@NoArgsConstructor
public class UpdateDocumentRequest {

    @NotBlank(message = "Document name is required")
    @Size(max = 255, message = "Document name must be at most 255 characters")
    private String name;

    @Size(max = 5000, message = "Notes must be at most 5000 characters")
    private String notes;
}
