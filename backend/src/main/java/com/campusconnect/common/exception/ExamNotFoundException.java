package com.campusconnect.common.exception;

/**
 * Purpose: Signals that a requested Exam could not be found.
 * Role: Thrown by service/repository lookups; mapped to HTTP 404 by GlobalExceptionHandler.
 */
public class ExamNotFoundException extends RuntimeException {
    public ExamNotFoundException(String message) {
        super(message);
    }
}
