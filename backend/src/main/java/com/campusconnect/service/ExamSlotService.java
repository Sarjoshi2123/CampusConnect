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

@Service
public class ExamSlotService {

    private final ExamSlotRepository examSlotRepository;
    private final ExamRepository examRepository;

    public ExamSlotService(ExamSlotRepository examSlotRepository, ExamRepository examRepository) {
        this.examSlotRepository = examSlotRepository;
        this.examRepository = examRepository;
    }

    public ExamSlot createSlot(String examId, LocalDate date, LocalTime startTime, int durationMinutes) {
        if (!examRepository.existsById(examId)) {
            throw new ExamNotFoundException("Exam not found: " + examId);
        }
        ExamSlot slot = new ExamSlot(UUID.randomUUID().toString(), examId, date, startTime, durationMinutes);
        return examSlotRepository.save(slot);
    }

    public ExamSlot getSlot(String examSlotId) {
        return examSlotRepository.findById(examSlotId)
                .orElseThrow(() -> new ExamSlotNotFoundException("Exam slot not found: " + examSlotId));
    }

    public List<ExamSlot> listSlotsForExam(String examId) {
        if (!examRepository.existsById(examId)) {
            throw new ExamNotFoundException("Exam not found: " + examId);
        }
        return examSlotRepository.findByExamId(examId);
    }
}
