package com.careercrack.careercrack.dtos;

import com.careercrack.careercrack.enums.DocumentType;
import com.careercrack.careercrack.validation.CrossFieldValidated;
import com.careercrack.careercrack.validation.ValidCrossFields;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Sent as multipart/form-data and bound with @ModelAttribute (not @RequestBody), because it carries a file.
 * There is no version field: re-uploading under the same name makes the next version automatically.
 */
@Getter
@Setter
@NoArgsConstructor
@ValidCrossFields
public class DocumentUploadRequest implements CrossFieldValidated {

    private MultipartFile file;

    @NotNull(message = "Document type is required")
    private DocumentType docType;

    // what the user calls it, e.g. "Backend resume"
    @NotBlank(message = "Document name is required")
    @Size(max = 255, message = "Document name must be at most 255 characters")
    private String name;

    // what changed in this version
    @Size(max = 5000, message = "Notes must be at most 5000 characters")
    private String notes;

    @Override
    public Map<String, String> crossFieldErrors() {
        Map<String, String> errors = new LinkedHashMap<>();
        if (file == null || file.isEmpty()) {
            errors.put("file", "A file is required");
        } else if (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()) {
            errors.put("file", "The file has no name");
        } else if (file.getOriginalFilename().length() > 255) {
            errors.put("file", "File name must be at most 255 characters");
        }
        return errors;
    }
}
