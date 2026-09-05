package com.campusconnect.repository;

import com.campusconnect.model.Student;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Purpose: Manages in-memory storage and retrieval of Student entities.
 * Role: Provides CRUD operations for Student objects using ConcurrentHashMap.
 * Important Assumptions: Student IDs are unique. Data is transient and resets on application restart.
 */
@Repository
public class StudentRepository {
    private final Map<String, Student> students = new ConcurrentHashMap<>();

    /**
     * Saves a student to the repository.
     * @param student The student to save.
     * @return The saved student.
     */
    public Student save(Student student) {
        students.put(student.getId(), student);
        return student;
    }

    /**
     * Finds a student by their ID.
     * @param id The ID of the student to find.
     * @return An Optional containing the student if found, empty otherwise.
     */
    public Optional<Student> findById(String id) {
        return Optional.ofNullable(students.get(id));
    }

    /**
     * Deletes a student by their ID.
     * @param id The ID of the student to delete.
     */
    public void deleteById(String id) {
        students.remove(id);
    }

    /**
     * Checks if a student with the given ID exists.
     * @param id The ID of the student to check.
     * @return true if the student exists, false otherwise.
     */
    public boolean existsById(String id) {
        return students.containsKey(id);
    }
}
