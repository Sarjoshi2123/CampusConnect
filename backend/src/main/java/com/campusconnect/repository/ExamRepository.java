package com.campusconnect.repository;

import com.campusconnect.model.Exam;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Purpose: Manages in-memory storage and retrieval of Exam entities.
 * Role: Provides CRUD operations for Exam objects using ConcurrentHashMap.
 * Important Assumptions: Exam IDs are unique. Data is transient and resets on application restart.
 */
@Repository
public class ExamRepository {
    private final Map<String, Exam> exams = new ConcurrentHashMap<>();

    /**
     * Saves an exam to the repository.
     * @param exam The exam to save.
     * @return The saved exam.
     */
    public Exam save(Exam exam) {
        exams.put(exam.getId(), exam);
        return exam;
    }

    /**
     * Finds an exam by its ID.
     * @param id The ID of the exam to find.
     * @return An Optional containing the exam if found, empty otherwise.
     */
    public Optional<Exam> findById(String id) {
        return Optional.ofNullable(exams.get(id));
    }

    /**
     * Deletes an exam by its ID.
     * @param id The ID of the exam to delete.
     */
    public void deleteById(String id) {
        exams.remove(id);
    }

    /**
     * Checks if an exam with the given ID exists.
     * @param id The ID of the exam to check.
     * @return true if the exam exists, false otherwise.
     */
    public boolean existsById(String id) {
        return exams.containsKey(id);
    }

    /**
     * Retrieves all exams in the repository.
     * @return A collection of all exams.
     */
    public Collection<Exam> findAll() {
        return exams.values();
    }
}
