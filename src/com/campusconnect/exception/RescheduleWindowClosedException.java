package com.campusconnect.exception;

/**
 * Thrown when a reschedule request is submitted after the current slot's check-in window has opened.
 */
public class RescheduleWindowClosedException extends CampusConnectException {
    public RescheduleWindowClosedException(String message) {
        super(message);
    }
}
