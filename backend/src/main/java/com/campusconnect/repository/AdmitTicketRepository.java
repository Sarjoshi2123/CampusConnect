package com.campusconnect.repository;

import com.campusconnect.model.AdmitTicket;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AdmitTicketRepository {
    private final Map<String, AdmitTicket> admitTickets = new ConcurrentHashMap<>();

    public AdmitTicket save(AdmitTicket admitTicket) {
        admitTickets.put(admitTicket.getId(), admitTicket);
        return admitTicket;
    }

    public Optional<AdmitTicket> findById(String id) {
        return Optional.ofNullable(admitTickets.get(id));
    }

    public void deleteById(String id) {
        admitTickets.remove(id);
    }

    public boolean existsById(String id) {
        return admitTickets.containsKey(id);
    }

    public Optional<AdmitTicket> findByRegistrationId(String registrationId) {
        return admitTickets.values().stream()
                .filter(ticket -> ticket.getRegistrationId().equals(registrationId))
                .findFirst();
    }
}
