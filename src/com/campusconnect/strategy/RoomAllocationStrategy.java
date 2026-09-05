package com.campusconnect.strategy;

import com.campusconnect.model.ProctoringRoom;
import java.util.List;
import java.util.Optional;

/**
 * Strategy interface for assigning students to available proctoring rooms.
 */
public interface RoomAllocationStrategy {
    /**
     * Selects an available proctoring room from the provided list based on strategy rules.
     *
     * @param rooms List of proctoring rooms configured for the slot
     * @return Optional containing the selected room, or empty if all rooms are full
     */
    Optional<ProctoringRoom> selectRoom(List<ProctoringRoom> rooms);
}
