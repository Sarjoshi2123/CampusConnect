package com.campusconnect.service;

import com.campusconnect.common.exception.ExamNotFoundException;
import com.campusconnect.common.exception.ExamSlotNotFoundException;
import com.campusconnect.model.ExamSlot;
import com.campusconnect.repository.ExamRepository;
import com.campusconnect.repository.ExamSlotRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Purpose: Basic CRUD orchestration for ExamSlot.
 * Role: Thin service sitting between ExamSlotController and ExamSlotRepository, so
 *       the controller never touches the repository directly. ExamSlot always
 *       belongs to an Exam (referenced by ID, not nested), so creation validates
 *       the parent exam exists first.
 */
@Service
public class ExamSlotService {

    private final ExamSlotRepository examSlotRepository;
    private final ExamRepository examRepository;

    public ExamSlotService(ExamSlotRepository examSlotRepository, ExamRepository examRepository) {
        this.examSlotRepository = examSlotRepository;
        this.examRepository = examRepository;
    }

    /**
     * Creates a new exam slot under the given exam.
     * @param examId          the parent exam ID.
     * @param date            the slot's date.
     * @param startTime       the slot's start time.
     * @param durationMinutes the slot's duration in minutes.
     * @return the created ExamSlot.
     */
    public ExamSlot createSlot(String examId, LocalDate date, LocalTime startTime, int durationMinutes) {
        if (!examRepository.existsById(examId)) {
            throw new ExamNotFoundException("Exam not found: " + examId);
        }
        ExamSlot slot = new ExamSlot(UUID.randomUUID().toString(), examId, date, startTime, durationMinutes);
        return examSlotRepository.save(slot);
    }

    /**
     * Retrieves an exam slot by ID.
     * @param examSlotId the exam slot ID.
     * @return the ExamSlot.
     */
    public ExamSlot getSlot(String examSlotId) {
        return examSlotRepository.findById(examSlotId)
                .orElseThrow(() -> new ExamSlotNotFoundException("Exam slot not found: " + examSlotId));
    }

    /**
     * Lists all slots belonging to an exam.
     * @param examId the parent exam ID.
     * @return the exam's slots.
     */
    public List<ExamSlot> listSlotsForExam(String examId) {
        if (!examRepository.existsById(examId)) {
            throw new ExamNotFoundException("Exam not found: " + examId);
        }
        return examSlotRepository.findByExamId(examId);
    }
}
