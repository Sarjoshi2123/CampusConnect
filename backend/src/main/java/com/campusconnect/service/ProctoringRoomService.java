package com.campusconnect.service;

import com.campusconnect.common.exception.ExamSlotNotFoundException;
import com.campusconnect.model.ProctoringRoom;
import com.campusconnect.repository.ExamSlotRepository;
import com.campusconnect.repository.ProctoringRoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Purpose: Basic CRUD orchestration for ProctoringRoom.
 * Role: Thin service sitting between ProctoringRoomController and
 *       ProctoringRoomRepository, so the controller never touches the repository
 *       directly. A room always belongs to an exam slot, so creation validates
 *       the parent slot exists first.
 */
@Service
public class ProctoringRoomService {

    private final ProctoringRoomRepository proctoringRoomRepository;
    private final ExamSlotRepository examSlotRepository;

    public ProctoringRoomService(ProctoringRoomRepository proctoringRoomRepository,
                                  ExamSlotRepository examSlotRepository) {
        this.proctoringRoomRepository = proctoringRoomRepository;
        this.examSlotRepository = examSlotRepository;
    }

    /**
     * Creates a new proctoring room under the given exam slot.
     * @param examSlotId the parent exam slot ID.
     * @param capacity   the room's seating capacity (must be positive).
     * @return the created ProctoringRoom.
     */
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

    /**
     * Lists all rooms belonging to an exam slot.
     * @param examSlotId the parent exam slot ID.
     * @return the slot's proctoring rooms.
     */
    public List<ProctoringRoom> listRoomsForSlot(String examSlotId) {
        if (!examSlotRepository.existsById(examSlotId)) {
            throw new ExamSlotNotFoundException("Exam slot not found: " + examSlotId);
        }
        return proctoringRoomRepository.findByExamSlotId(examSlotId);
    }
}
