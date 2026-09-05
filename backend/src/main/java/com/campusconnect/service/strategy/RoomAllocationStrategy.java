package com.campusconnect.service.strategy;

import com.campusconnect.model.ProctoringRoom;

import java.util.List;
import java.util.Optional;

/**
 * Purpose: Defines the pluggable algorithm for choosing which proctoring room a new
 *          registration should be seated in, among the rooms belonging to an exam slot.
 * Role: Strategy interface (GoF Strategy pattern). Implementations are Spring beans;
 *       AppConfig selects the active one via @Primary (or a caller can request a
 *       specific one via @Qualifier using the bean name). RegistrationService depends
 *       only on this interface, never on a concrete strategy, so the allocation
 *       policy can change without touching registration/reschedule logic.
 * Important Assumptions: Implementations must not mutate room occupancy themselves —
 *       they only select a candidate; the caller is responsible for incrementing
 *       occupancy once the room is actually committed to a registration.
 */
public interface RoomAllocationStrategy {

    /**
     * Selects a room with available capacity from the given candidates.
     * @param rooms the rooms belonging to the target exam slot (may be empty).
     * @return an Optional containing the selected room, or empty if none of the
     *         given rooms currently has available capacity.
     */
    Optional<ProctoringRoom> selectRoom(List<ProctoringRoom> rooms);
}
