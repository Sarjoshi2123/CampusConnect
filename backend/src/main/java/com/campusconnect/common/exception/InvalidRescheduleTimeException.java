package com.campusconnect.common.exception;

/**
 * Purpose: Signals that a reschedule was submitted after the check-in window for the
 *          student's current exam slot had already opened, so it is too late to move.
 * Role: Thrown by RegistrationService.reschedule(); mapped to HTTP 400 by
 *       GlobalExceptionHandler.
 */
public class InvalidRescheduleTimeException extends RuntimeException {
    public InvalidRescheduleTimeException(String message) {
        super(message);
    }
}
