package com.campusconnect.service.strategy;

import com.campusconnect.model.ProctoringRoom;

import java.util.List;
import java.util.Optional;

public interface RoomAllocationStrategy {

    Optional<ProctoringRoom> selectRoom(List<ProctoringRoom> rooms);
}
