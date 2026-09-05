package com.campusconnect.common.exception;

/**
 * Purpose: Signals that a requested ExamSlot could not be found.
 * Role: Thrown by service/repository lookups; mapped to HTTP 404 by GlobalExceptionHandler.
 */
public class ExamSlotNotFoundException extends RuntimeException {
    public ExamSlotNotFoundException(String message) {
        super(message);
    }
}
