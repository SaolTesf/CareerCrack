package com.careercrack.careercrack.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ContactResponse {
    private Long id;
    private Long companyId;
    private String companyName;
    private String name;
    private String contactRole;
    private String email;
    private String phone;
    private String linkedinUrl;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
