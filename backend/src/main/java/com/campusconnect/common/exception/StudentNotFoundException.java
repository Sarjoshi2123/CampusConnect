package com.campusconnect.common.exception;

/**
 * Purpose: Signals that a requested Student could not be found.
 * Role: Thrown by service/repository lookups; mapped to HTTP 404 by GlobalExceptionHandler.
 */
public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(String message) {
        super(message);
    }
}
