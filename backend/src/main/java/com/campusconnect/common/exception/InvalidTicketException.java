package com.campusconnect.common.exception;

/**
 * Purpose: Signals that an admit ticket presented at check-in does not exist or does
 *          not correspond to a currently valid (active) registration — e.g. it was
 *          superseded by a reschedule.
 * Role: Thrown by AdmitTicketService.checkIn(); mapped to HTTP 400 by
 *       GlobalExceptionHandler.
 */
public class InvalidTicketException extends RuntimeException {
    public InvalidTicketException(String message) {
        super(message);
    }
}
