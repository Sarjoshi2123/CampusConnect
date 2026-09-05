package com.campusconnect.exception;

/**
 * Thrown when a student attempts to register or reschedule into a slot
 * that overlaps in time with another slot they are already registered for.
 */
public class SchedulingConflictException extends CampusConnectException {
    public SchedulingConflictException(String message) {
        super(message);
    }

    public SchedulingConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
