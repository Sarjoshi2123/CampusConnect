package com.campusconnect.service.strategy;

import com.campusconnect.model.ProctoringRoom;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class BalancedRoomAllocationStrategy implements RoomAllocationStrategy {

    @Override
    public Optional<ProctoringRoom> selectRoom(List<ProctoringRoom> rooms) {
        return rooms.stream()
                .filter(room -> !room.isFull())
                .max(Comparator.comparingInt(ProctoringRoom::getAvailableCapacity)
                        .thenComparing(Comparator.comparing(ProctoringRoom::getId).reversed()));
    }
}
