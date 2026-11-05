package com.careercrack.careercrack.exceptions;

import java.io.Serial;

/** The request is fine but the current data does not allow it, e.g. deleting a company that still has applications. Becomes a 409. */
public class ConflictException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ConflictException(String message) {
        super(message);
    }
}
