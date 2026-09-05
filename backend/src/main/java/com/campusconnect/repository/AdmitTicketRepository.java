package com.campusconnect.repository;

import com.campusconnect.model.AdmitTicket;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Purpose: Manages in-memory storage and retrieval of AdmitTicket entities.
 * Role: Provides CRUD operations for AdmitTicket objects using ConcurrentHashMap.
 * Important Assumptions: AdmitTicket IDs are unique. Data is transient and resets on application restart.
 */
@Repository
public class AdmitTicketRepository {
    private final Map<String, AdmitTicket> admitTickets = new ConcurrentHashMap<>();

    /**
     * Saves an admit ticket to the repository.
     * @param admitTicket The admit ticket to save.
     * @return The saved admit ticket.
     */
    public AdmitTicket save(AdmitTicket admitTicket) {
        admitTickets.put(admitTicket.getId(), admitTicket);
        return admitTicket;
    }

    /**
     * Finds an admit ticket by its ID.
     * @param id The ID of the admit ticket to find.
     * @return An Optional containing the admit ticket if found, empty otherwise.
     */
    public Optional<AdmitTicket> findById(String id) {
        return Optional.ofNullable(admitTickets.get(id));
    }

    /**
     * Deletes an admit ticket by its ID.
     * @param id The ID of the admit ticket to delete.
     */
    public void deleteById(String id) {
        admitTickets.remove(id);
    }

    /**
     * Checks if an admit ticket with the given ID exists.
     * @param id The ID of the admit ticket to check.
     * @return true if the admit ticket exists, false otherwise.
     */
    public boolean existsById(String id) {
        return admitTickets.containsKey(id);
    }

    /**
     * Finds an admit ticket by its registration ID.
     * @param registrationId The ID of the registration.
     * @return An Optional containing the admit ticket if found, empty otherwise.
     */
    public Optional<AdmitTicket> findByRegistrationId(String registrationId) {
        return admitTickets.values().stream()
                .filter(ticket -> ticket.getRegistrationId().equals(registrationId))
                .findFirst();
    }
}
