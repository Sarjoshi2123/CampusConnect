package com.campusconnect.common.exception;

/**
 * Purpose: Signals that an admit ticket presented at check-in has already been used
 *          (single-use enforcement).
 * Role: Thrown by AdmitTicketService.checkIn(); mapped to HTTP 400 by
 *       GlobalExceptionHandler.
 */
public class AdmitTicketAlreadyUsedException extends RuntimeException {
    public AdmitTicketAlreadyUsedException(String message) {
        super(message);
    }
}
