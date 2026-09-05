package com.campusconnect.controller;

import com.campusconnect.common.dto.ExamCreateRequest;
import com.campusconnect.common.dto.ExamResponse;
import com.campusconnect.model.Exam;
import com.campusconnect.service.ExamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exams")
public class ExamController {

    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @PostMapping
    public ResponseEntity<ExamResponse> createExam(@RequestBody ExamCreateRequest request) {
        Exam exam = examService.createExam(request.getName(), request.getDescription());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(exam));
    }

    @GetMapping("/{examId}")
    public ResponseEntity<ExamResponse> getExam(@PathVariable String examId) {
        return ResponseEntity.ok(toResponse(examService.getExam(examId)));
    }

    @GetMapping
    public ResponseEntity<List<ExamResponse>> listExams() {
        List<ExamResponse> responses = examService.listExams().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    private ExamResponse toResponse(Exam exam) {
        return new ExamResponse(exam.getId(), exam.getName(), exam.getDescription());
    }
}

