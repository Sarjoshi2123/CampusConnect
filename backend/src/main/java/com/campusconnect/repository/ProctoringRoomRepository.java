package com.campusconnect.repository;

import com.campusconnect.model.ProctoringRoom;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Purpose: Manages in-memory storage and retrieval of ProctoringRoom entities.
 * Role: Provides CRUD operations for ProctoringRoom objects using ConcurrentHashMap.
 * Important Assumptions: ProctoringRoom IDs are unique. Data is transient and resets on application restart.
 */
@Repository
public class ProctoringRoomRepository {
    private final Map<String, ProctoringRoom> rooms = new ConcurrentHashMap<>();

    /**
     * Saves a proctoring room to the repository.
     * @param room The proctoring room to save.
     * @return The saved proctoring room.
     */
    public ProctoringRoom save(ProctoringRoom room) {
        rooms.put(room.getId(), room);
        return room;
    }

    /**
     * Finds a proctoring room by its ID.
     * @param id The ID of the proctoring room to find.
     * @return An Optional containing the room if found, empty otherwise.
     */
    public Optional<ProctoringRoom> findById(String id) {
        return Optional.ofNullable(rooms.get(id));
    }

    /**
     * Deletes a proctoring room by its ID.
     * @param id The ID of the proctoring room to delete.
     */
    public void deleteById(String id) {
        rooms.remove(id);
    }

    /**
     * Checks if a proctoring room with the given ID exists.
     * @param id The ID of the proctoring room to check.
     * @return true if the room exists, false otherwise.
     */
    public boolean existsById(String id) {
        return rooms.containsKey(id);
    }

    /**
     * Finds all proctoring rooms for a given exam slot ID.
     * @param examSlotId The ID of the exam slot.
     * @return A list of proctoring rooms for the specified exam slot.
     */
    public List<ProctoringRoom> findByExamSlotId(String examSlotId) {
        return rooms.values().stream()
                .filter(room -> room.getExamSlotId().equals(examSlotId))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all proctoring rooms in the repository.
     * @return A collection of all proctoring rooms.
     */
    public Collection<ProctoringRoom> findAll() {
        return rooms.values();
    }
}
