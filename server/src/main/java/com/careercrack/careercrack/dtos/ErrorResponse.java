package com.careercrack.careercrack.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
    private int error;
    private String message;

    public ErrorResponse(int error, String message) {
        this.error = error;
        this.message = message;
    }
}
