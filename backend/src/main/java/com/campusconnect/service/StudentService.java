package com.campusconnect.service;

import com.campusconnect.common.exception.StudentNotFoundException;
import com.campusconnect.model.Student;
import com.campusconnect.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Purpose: Basic CRUD orchestration for Student.
 * Role: Thin service sitting between StudentController and StudentRepository, so
 *       the controller never touches the repository directly (keeping the same
 *       Controller -> Service -> Repository layering used by the rest of the
 *       backend, e.g. RegistrationService).
 */
@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * Creates a new student.
     * @param name the student's name.
     * @return the created Student.
     */
    public Student createStudent(String name) {
        Student student = new Student(UUID.randomUUID().toString(), name);
        return studentRepository.save(student);
    }

    /**
     * Retrieves a student by ID.
     * @param studentId the student ID.
     * @return the Student.
     */
    public Student getStudent(String studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found: " + studentId));
    }
}
