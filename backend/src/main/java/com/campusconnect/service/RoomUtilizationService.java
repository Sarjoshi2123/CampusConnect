package com.campusconnect.service;

import com.campusconnect.common.dto.RoomUtilizationResponse;
import com.campusconnect.common.exception.ExamSlotNotFoundException;
import com.campusconnect.model.ProctoringRoom;
import com.campusconnect.repository.ExamSlotRepository;
import com.campusconnect.repository.ProctoringRoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Purpose: Answers "how full is this exam slot" queries.
 * Role: Read-only service returning, per proctoring room belonging to a slot, its
 *       capacity, filled seats, and remaining available seats.
 */
@Service
public class RoomUtilizationService {

    private final ExamSlotRepository examSlotRepository;
    private final ProctoringRoomRepository proctoringRoomRepository;

    public RoomUtilizationService(ExamSlotRepository examSlotRepository,
                                   ProctoringRoomRepository proctoringRoomRepository) {
        this.examSlotRepository = examSlotRepository;
        this.proctoringRoomRepository = proctoringRoomRepository;
    }

    /**
     * Reports filled vs. available capacity for every proctoring room belonging
     * to the given exam slot.
     * @param examSlotId the exam slot to report on.
     * @return one RoomUtilizationResponse per room in that slot.
     */
    public List<RoomUtilizationResponse> getUtilizationForSlot(String examSlotId) {
        if (!examSlotRepository.existsById(examSlotId)) {
            throw new ExamSlotNotFoundException("Exam slot not found: " + examSlotId);
        }
        List<ProctoringRoom> rooms = proctoringRoomRepository.findByExamSlotId(examSlotId);
        return rooms.stream()
                .map(room -> new RoomUtilizationResponse(
                        examSlotId, room.getId(), room.getCapacity(),
                        room.getCurrentOccupancy(), room.getAvailableCapacity()))
                .collect(Collectors.toList());
    }
}
