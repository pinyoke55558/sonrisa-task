package com.example.alerts.exception;

/**
 * A requested record doesn't exist, is deleted, or isn't visible to the caller. Answered with 404
 * by {@link GlobalExceptionHandler}.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String resource, Long id) {
        super(resource + " " + id + " not found");
    }
}
