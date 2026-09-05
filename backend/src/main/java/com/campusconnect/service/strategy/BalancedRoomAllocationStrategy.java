package com.campusconnect.service.strategy;

import com.campusconnect.model.ProctoringRoom;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Purpose: RoomAllocationStrategy implementation that spreads students evenly across
 *          all rooms of an exam slot, rather than filling one room before the next.
 * Role: Concrete Strategy. Selects the non-full room with the greatest available
 *       capacity, so occupancy rises roughly in lockstep across every room. Room ID
 *       is used only as a deterministic tie-breaker when two rooms have equal
 *       available capacity.
 * Important Assumptions: Instantiated as a bean by AppConfig (not component-scanned —
 *       see FillFirstRoomAllocationStrategy for why @Component is deliberately
 *       omitted here).
 */
public class BalancedRoomAllocationStrategy implements RoomAllocationStrategy {

    @Override
    public Optional<ProctoringRoom> selectRoom(List<ProctoringRoom> rooms) {
        return rooms.stream()
                .filter(room -> !room.isFull())
                .max(Comparator.comparingInt(ProctoringRoom::getAvailableCapacity)
                        .thenComparing(Comparator.comparing(ProctoringRoom::getId).reversed()));
    }
}
