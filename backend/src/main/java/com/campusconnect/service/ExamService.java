package com.campusconnect.service;

import com.campusconnect.common.exception.ExamNotFoundException;
import com.campusconnect.model.Exam;
import com.campusconnect.repository.ExamRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

/**
 * Purpose: Basic CRUD orchestration for Exam.
 * Role: Thin service sitting between ExamController and ExamRepository, so the
 *       controller never touches the repository directly.
 */
@Service
public class ExamService {

    private final ExamRepository examRepository;

    public ExamService(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    /**
     * Creates a new exam.
     * @param name        the exam name.
     * @param description the exam description.
     * @return the created Exam.
     */
    public Exam createExam(String name, String description) {
        Exam exam = new Exam(UUID.randomUUID().toString(), name, description);
        return examRepository.save(exam);
    }

    /**
     * Retrieves an exam by ID.
     * @param examId the exam ID.
     * @return the Exam.
     */
    public Exam getExam(String examId) {
        return examRepository.findById(examId)
                .orElseThrow(() -> new ExamNotFoundException("Exam not found: " + examId));
    }

    /**
     * Lists all exams.
     * @return all exams currently in the system.
     */
    public Collection<Exam> listExams() {
        return examRepository.findAll();
    }
}
