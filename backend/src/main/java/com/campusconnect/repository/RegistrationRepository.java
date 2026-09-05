package com.campusconnect.repository;

import com.campusconnect.model.Registration;
import com.campusconnect.model.Registration.Status;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Purpose: Manages in-memory storage and retrieval of Registration entities.
 * Role: Provides CRUD operations for Registration objects using ConcurrentHashMap.
 * Important Assumptions: Registration IDs are unique. Data is transient and resets on application restart.
 */
@Repository
public class RegistrationRepository {
    private final Map<String, Registration> registrations = new ConcurrentHashMap<>();

    /**
     * Saves a registration to the repository.
     * @param registration The registration to save.
     * @return The saved registration.
     */
    public Registration save(Registration registration) {
        registrations.put(registration.getId(), registration);
        return registration;
    }

    /**
     * Finds a registration by its ID.
     * @param id The ID of the registration to find.
     * @return An Optional containing the registration if found, empty otherwise.
     */
    public Optional<Registration> findById(String id) {
        return Optional.ofNullable(registrations.get(id));
    }

    /**
     * Deletes a registration by its ID.
     * @param id The ID of the registration to delete.
     */
    public void deleteById(String id) {
        registrations.remove(id);
    }

    /**
     * Checks if a registration with the given ID exists.
     * @param id The ID of the registration to check.
     * @return true if the registration exists, false otherwise.
     */
    public boolean existsById(String id) {
        return registrations.containsKey(id);
    }

    /**
     * Finds all active registrations for a given student.
     * @param studentId The ID of the student.
     * @return A list of active registrations for the specified student.
     */
    public List<Registration> findActiveRegistrationsByStudentId(String studentId) {
        return registrations.values().stream()
                .filter(reg -> reg.getStudentId().equals(studentId) && reg.getStatus() == Status.REGISTERED)
                .collect(Collectors.toList());
    }

    /**
     * Finds an active registration for a student for a specific exam slot.
     * @param studentId The ID of the student.
     * @param examSlotId The ID of the exam slot.
     * @return An Optional containing the active registration if found, empty otherwise.
     */
    public Optional<Registration> findActiveRegistrationByStudentIdAndExamSlotId(String studentId, String examSlotId) {
        return registrations.values().stream()
                .filter(reg -> reg.getStudentId().equals(studentId) &&
                                 reg.getExamSlotId().equals(examSlotId) &&
                                 reg.getStatus() == Status.REGISTERED)
                .findFirst();
    }

    /**
     * Finds all registrations for a given student.
     * @param studentId The ID of the student.
     * @return A list of all registrations for the specified student, regardless of status.
     */
    public List<Registration> findAllRegistrationsByStudentId(String studentId) {
        return registrations.values().stream()
                .filter(reg -> reg.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    /**
     * Finds all registrations for a given exam slot.
     * @param examSlotId The ID of the exam slot.
     * @return A list of all registrations for the specified exam slot.
     */
    public List<Registration> findByExamSlotId(String examSlotId) {
        return registrations.values().stream()
                .filter(reg -> reg.getExamSlotId().equals(examSlotId))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all registrations in the repository.
     * @return A collection of all registrations.
     */
    public Collection<Registration> findAll() {
        return registrations.values();
    }
}
