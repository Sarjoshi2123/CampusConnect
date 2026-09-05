package com.campusconnect.exception;

/**
 * Base custom runtime exception for CampusConnect domain errors.
 */
public class CampusConnectException extends RuntimeException {
    public CampusConnectException(String message) {
        super(message);
    }

    public CampusConnectException(String message, Throwable cause) {
        super(message, cause);
    }
}
