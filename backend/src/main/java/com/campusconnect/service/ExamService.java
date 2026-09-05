package com.campusconnect.service;

import com.campusconnect.common.exception.ExamNotFoundException;
import com.campusconnect.model.Exam;
import com.campusconnect.repository.ExamRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

@Service
public class ExamService {

    private final ExamRepository examRepository;

    public ExamService(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    public Exam createExam(String name, String description) {
        Exam exam = new Exam(UUID.randomUUID().toString(), name, description);
        return examRepository.save(exam);
    }

    public Exam getExam(String examId) {
        return examRepository.findById(examId)
                .orElseThrow(() -> new ExamNotFoundException("Exam not found: " + examId));
    }

    public Collection<Exam> listExams() {
        return examRepository.findAll();
    }
}
