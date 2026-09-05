package com.campusconnect.strategy;

import com.campusconnect.model.ProctoringRoom;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Balanced allocation strategy: Selects the room with the maximum remaining available capacity
 * to distribute students evenly across all proctoring rooms.
 */
public class BalancedAllocationStrategy implements RoomAllocationStrategy {
    @Override
    public Optional<ProctoringRoom> selectRoom(List<ProctoringRoom> rooms) {
        if (rooms == null || rooms.isEmpty()) return Optional.empty();

        return rooms.stream()
                .filter(room -> !room.isFull())
                .max(Comparator.comparingInt(ProctoringRoom::getAvailableCapacity));
    }
}
