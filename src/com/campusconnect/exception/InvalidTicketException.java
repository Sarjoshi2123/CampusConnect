package com.campusconnect.exception;

/**
 * Thrown when an admit ticket is invalid, not found, or already used during check-in.
 */
public class InvalidTicketException extends CampusConnectException {
    public InvalidTicketException(String message) {
        super(message);
    }
}
