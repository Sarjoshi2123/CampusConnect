package com.campusconnect.service;

import com.campusconnect.model.AdmitTicket;
import com.campusconnect.model.Registration;

public class RegistrationResult {
    private final Registration registration;
    private final AdmitTicket admitTicket;

    public RegistrationResult(Registration registration, AdmitTicket admitTicket) {
        this.registration = registration;
        this.admitTicket = admitTicket;
    }

    public Registration getRegistration() {
        return registration;
    }

    public AdmitTicket getAdmitTicket() {
        return admitTicket;
    }
}
