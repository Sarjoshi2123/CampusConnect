package com.campusconnect.controller;

import com.campusconnect.common.dto.StudentCreateRequest;
import com.campusconnect.common.dto.StudentResponse;
import com.campusconnect.model.Student;
import com.campusconnect.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Purpose: REST endpoints for creating and retrieving students.
 * Role: Controller layer — delegates all logic to StudentService and only ever
 *       returns StudentResponse DTOs, never the Student domain entity.
 */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * Creates a new student.
     * @param request the student's name.
     * @return the created student, as a StudentResponse.
     */
    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody StudentCreateRequest request) {
        Student student = studentService.createStudent(request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(student));
    }

    /**
     * Retrieves a student by ID.
     * @param studentId the student ID.
     * @return the student, as a StudentResponse.
     */
    @GetMapping("/{studentId}")
    public ResponseEntity<StudentResponse> getStudent(@PathVariable String studentId) {
        return ResponseEntity.ok(toResponse(studentService.getStudent(studentId)));
    }

    private StudentResponse toResponse(Student student) {
        return new StudentResponse(student.getId(), student.getName());
    }
}
