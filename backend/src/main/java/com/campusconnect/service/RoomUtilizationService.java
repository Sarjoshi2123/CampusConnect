package com.campusconnect.service;

import com.campusconnect.common.dto.RoomUtilizationResponse;
import com.campusconnect.common.exception.ExamSlotNotFoundException;
import com.campusconnect.model.ProctoringRoom;
import com.campusconnect.repository.ExamSlotRepository;
import com.campusconnect.repository.ProctoringRoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomUtilizationService {

    private final ExamSlotRepository examSlotRepository;
    private final ProctoringRoomRepository proctoringRoomRepository;

    public RoomUtilizationService(ExamSlotRepository examSlotRepository,
                                   ProctoringRoomRepository proctoringRoomRepository) {
        this.examSlotRepository = examSlotRepository;
        this.proctoringRoomRepository = proctoringRoomRepository;
    }

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
