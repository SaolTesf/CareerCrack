package com.careercrack.careercrack.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L; // fixes "serializable class com.careercrack.careercrack.exceptions.ResourceNotFoundException has no definition of serialVersionUID"
    public ResourceNotFoundException(String message) { super(message); }
}