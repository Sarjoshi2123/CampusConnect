package com.campusconnect.common.exception;

/**
 * Purpose: Signals that a requested AdmitTicket could not be found by direct lookup
 *          (e.g. GET by ID), as distinct from an invalid ticket presented at check-in.
 * Role: Thrown by service/repository lookups; mapped to HTTP 404 by GlobalExceptionHandler.
 */
public class AdmitTicketNotFoundException extends RuntimeException {
    public AdmitTicketNotFoundException(String message) {
        super(message);
    }
}
