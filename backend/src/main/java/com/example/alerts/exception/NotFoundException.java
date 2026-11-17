package com.example.alerts.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/** A requested record doesn't exist, is deleted, or isn't visible to the caller. Answered with 404. */
public class NotFoundException extends ResponseStatusException {

    public NotFoundException(String resource, Long id) {
        super(HttpStatus.NOT_FOUND, resource + " " + id + " not found");
    }
}
