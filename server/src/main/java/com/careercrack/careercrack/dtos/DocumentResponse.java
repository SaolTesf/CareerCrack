package com.careercrack.careercrack.dtos;

import com.careercrack.careercrack.enums.DocumentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** There is deliberately no storageKey here: where the file lives on the server is never sent to the client. */
@Getter
@Setter
@NoArgsConstructor
public class DocumentResponse {
    private Long id;
    private DocumentType docType;
    private String name;
    private Integer version;
    private String fileName;
    private String contentType;
    private Long fileSize;
    private String notes;
    private LocalDateTime createdAt;
}
