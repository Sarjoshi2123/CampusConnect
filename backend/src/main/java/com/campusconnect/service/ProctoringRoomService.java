package com.campusconnect.service;

import com.campusconnect.common.exception.ExamSlotNotFoundException;
import com.campusconnect.model.ProctoringRoom;
import com.campusconnect.repository.ExamSlotRepository;
import com.campusconnect.repository.ProctoringRoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProctoringRoomService {

    private final ProctoringRoomRepository proctoringRoomRepository;
    private final ExamSlotRepository examSlotRepository;

    public ProctoringRoomService(ProctoringRoomRepository proctoringRoomRepository,
                                  ExamSlotRepository examSlotRepository) {
        this.proctoringRoomRepository = proctoringRoomRepository;
        this.examSlotRepository = examSlotRepository;
    }

    public ProctoringRoom createRoom(String examSlotId, int capacity) {
        if (!examSlotRepository.existsById(examSlotId)) {
            throw new ExamSlotNotFoundException("Exam slot not found: " + examSlotId);
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Room capacity must be positive, got: " + capacity);
        }
        ProctoringRoom room = new ProctoringRoom(UUID.randomUUID().toString(), examSlotId, capacity);
        return proctoringRoomRepository.save(room);
    }

    public List<ProctoringRoom> listRoomsForSlot(String examSlotId) {
        if (!examSlotRepository.existsById(examSlotId)) {
            throw new ExamSlotNotFoundException("Exam slot not found: " + examSlotId);
        }
        return proctoringRoomRepository.findByExamSlotId(examSlotId);
    }
}
