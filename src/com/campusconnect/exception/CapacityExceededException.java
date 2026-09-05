package com.campusconnect.exception;

/**
 * Thrown when all proctoring rooms for a given exam slot are at maximum capacity.
 */
public class CapacityExceededException extends CampusConnectException {
    public CapacityExceededException(String message) {
        super(message);
    }
}
