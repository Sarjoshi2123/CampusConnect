package com.campusconnect.service.strategy;

import com.campusconnect.model.ProctoringRoom;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Purpose: RoomAllocationStrategy implementation that fills one room to capacity
 *          before ever placing a student in the next room.
 * Role: Concrete Strategy. Iterates candidate rooms in a stable order (by room ID)
 *       and returns the first one that still has available capacity, so a room only
 *       reaches "full" before the next room receives its first occupant.
 * Important Assumptions: Instantiated as a bean by AppConfig (not component-scanned —
 *       AppConfig already builds it via `new FillFirstRoomAllocationStrategy()` in an
 *       @Bean factory method), so this class intentionally carries no @Component
 *       annotation to avoid a duplicate bean definition. Room ID ordering is used as
 *       a stable, deterministic tie-breaker across calls; it does not imply any
 *       real-world room numbering.
 */
public class FillFirstRoomAllocationStrategy implements RoomAllocationStrategy {

    @Override
    public Optional<ProctoringRoom> selectRoom(List<ProctoringRoom> rooms) {
        return rooms.stream()
                .filter(room -> !room.isFull())
                .min(Comparator.comparing(ProctoringRoom::getId));
    }
}
