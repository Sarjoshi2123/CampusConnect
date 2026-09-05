package com.campusconnect.common.exception;

/**
 * Purpose: Signals that a student already has another active registration whose exam
 *          slot overlaps in time with the slot being requested (registration or
 *          reschedule), regardless of which exam that other slot belongs to.
 * Role: Thrown by RegistrationService's conflict-check step; mapped to HTTP 409
 *       (Conflict) by GlobalExceptionHandler.
 */
public class SlotConflictException extends RuntimeException {
    public SlotConflictException(String message) {
        super(message);
    }
}
