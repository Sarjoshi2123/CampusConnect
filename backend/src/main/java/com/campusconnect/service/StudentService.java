package com.campusconnect.service;

import com.campusconnect.common.exception.StudentNotFoundException;
import com.campusconnect.model.Student;
import com.campusconnect.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(String name) {
        Student student = new Student(UUID.randomUUID().toString(), name);
        return studentRepository.save(student);
    }

    public Student getStudent(String studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found: " + studentId));
    }
}
