package com.campusconnect.strategy;

import com.campusconnect.model.ProctoringRoom;
import java.util.List;
import java.util.Optional;

/**
 * Fill-first strategy: Fills rooms in sequence until max capacity is reached before moving to the next room.
 */
public class FirstFitAllocationStrategy implements RoomAllocationStrategy {
    @Override
    public Optional<ProctoringRoom> selectRoom(List<ProctoringRoom> rooms) {
        if (rooms == null) return Optional.empty();
        
        return rooms.stream()
                .filter(room -> !room.isFull())
                .findFirst();
    }
}
