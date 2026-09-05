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

@Service
public class AdmitTicketService {

    private final AdmitTicketRepository admitTicketRepository;
    private final RegistrationRepository registrationRepository;

    public AdmitTicketService(AdmitTicketRepository admitTicketRepository,
                               RegistrationRepository registrationRepository) {
        this.admitTicketRepository = admitTicketRepository;
        this.registrationRepository = registrationRepository;
    }

    public AdmitTicket getTicket(String ticketId) {
        return admitTicketRepository.findById(ticketId)
                .orElseThrow(() -> new AdmitTicketNotFoundException("Admit ticket not found: " + ticketId));
    }

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
