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

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody StudentCreateRequest request) {
        Student student = studentService.createStudent(request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(student));
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<StudentResponse> getStudent(@PathVariable String studentId) {
        return ResponseEntity.ok(toResponse(studentService.getStudent(studentId)));
    }

    private StudentResponse toResponse(Student student) {
        return new StudentResponse(student.getId(), student.getName());
    }
}

