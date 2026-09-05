package com.campusconnect.service;

import com.campusconnect.model.AdmitTicket;
import com.campusconnect.model.Registration;

/**
 * Purpose: Internal carrier pairing a freshly created/updated Registration with the
 *          AdmitTicket issued alongside it.
 * Role: Return type for RegistrationService#register and #reschedule. Deliberately
 *       lives in the service package (not common.dto) — it is never serialized
 *       directly; controllers map it into RegistrationResponse so domain entities
 *       are never exposed through the REST layer.
 */
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
