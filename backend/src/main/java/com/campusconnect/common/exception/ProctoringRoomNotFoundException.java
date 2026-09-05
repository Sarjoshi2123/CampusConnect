package com.campusconnect.common.exception;

/**
 * Purpose: Signals that a requested ProctoringRoom could not be found.
 * Role: Thrown by service/repository lookups; mapped to HTTP 404 by GlobalExceptionHandler.
 */
public class ProctoringRoomNotFoundException extends RuntimeException {
    public ProctoringRoomNotFoundException(String message) {
        super(message);
    }
}
