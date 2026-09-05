package com.campusconnect.service;

import com.campusconnect.common.exception.AdmitTicketAlreadyUsedException;
import com.campusconnect.common.exception.AdmitTicketNotFoundException;
import com.campusconnect.common.exception.InvalidTicketException;
import com.campusconnect.common.exception.RegistrationNotFoundException;
import com.campusconnect.model.AdmitTicket;
import com.campusconnect.model.Registration;
import com.campusconnect.model.Registration.Status;
import com.campusconnect.repository.AdmitTicketRepository;
import com.campusconnect.repository.RegistrationRepository;
import org.springframework.stereotype.Service;

/**
 * Purpose: Validates admit tickets presented at exam check-in and enforces
 *          single-use semantics.
 * Role: Service-layer owner of the check-in workflow. A ticket is accepted for
 *       check-in only if it exists, has not already been used, and its
 *       registration is still in REGISTERED status (a registration that was
 *       cancelled by a reschedule, or already marked NO_SHOW, is not eligible
 *       even if the ticket object itself is technically still unused). On
 *       success, the ticket is flipped to used and the registration is moved to
 *       CHECKED_IN.
 * Important Assumptions: Checks run in a deliberate order so the two rejection
 *       reasons never get conflated: (1) unknown ticket ID -> InvalidTicketException;
 *       (2) ticket already flagged used (i.e. this exact ticket was already
 *       checked in) -> AdmitTicketAlreadyUsedException, checked BEFORE looking at
 *       registration status, since a successful check-in also moves the
 *       registration off REGISTERED and a second attempt on the same ticket must
 *       still read as "already used", not "invalid"; (3) otherwise, a registration
 *       no longer in REGISTERED status (superseded by a reschedule, or marked
 *       NO_SHOW) -> InvalidTicketException. This split matches the two distinct
 *       edge cases called out in the spec ("invalid" vs. "already-used").
 */
@Service
public class AdmitTicketService {

    private final AdmitTicketRepository admitTicketRepository;
    private final RegistrationRepository registrationRepository;

    public AdmitTicketService(AdmitTicketRepository admitTicketRepository,
                               RegistrationRepository registrationRepository) {
        this.admitTicketRepository = admitTicketRepository;
        this.registrationRepository = registrationRepository;
    }

    /**
     * Retrieves an admit ticket by ID, for direct lookups (e.g. a GET endpoint).
     * @param ticketId the admit ticket ID.
     * @return the AdmitTicket.
     */
    public AdmitTicket getTicket(String ticketId) {
        return admitTicketRepository.findById(ticketId)
                .orElseThrow(() -> new AdmitTicketNotFoundException("Admit ticket not found: " + ticketId));
    }

    /**
     * Validates and consumes an admit ticket at check-in.
     * @param ticketId the admit ticket ID presented by the student.
     * @return the now-used AdmitTicket.
     */
    public AdmitTicket checkIn(String ticketId) {
        AdmitTicket ticket = admitTicketRepository.findById(ticketId)
                .orElseThrow(() -> new InvalidTicketException("Admit ticket not found or invalid: " + ticketId));

        Registration registration = registrationRepository.findById(ticket.getRegistrationId())
                .orElseThrow(() -> new RegistrationNotFoundException(
                        "Registration not found: " + ticket.getRegistrationId()));

        if (ticket.isUsed()) {
            throw new AdmitTicketAlreadyUsedException("Admit ticket already used: " + ticketId);
        }

        if (registration.getStatus() != Status.REGISTERED) {
            throw new InvalidTicketException(
                    "Admit ticket " + ticketId + " is no longer valid for check-in (registration status: "
                            + registration.getStatus() + ")");
        }

        ticket.setUsed(true);
        admitTicketRepository.save(ticket);

        registration.setStatus(Status.CHECKED_IN);
        registrationRepository.save(registration);

        return ticket;
    }
}
