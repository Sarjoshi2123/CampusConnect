package com.campusconnect.common.exception;

public class AdmitTicketAlreadyUsedException extends RuntimeException {
    public AdmitTicketAlreadyUsedException(String message) {
        super(message);
    }
}
