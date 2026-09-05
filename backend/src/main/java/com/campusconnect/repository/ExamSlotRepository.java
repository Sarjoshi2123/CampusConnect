package com.campusconnect.repository;

import com.campusconnect.model.ExamSlot;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Purpose: Manages in-memory storage and retrieval of ExamSlot entities.
 * Role: Provides CRUD operations for ExamSlot objects using ConcurrentHashMap, mirroring
 *       the pattern used by the other repositories in this package (ExamRepository,
 *       StudentRepository, etc.). This repository was missing even though ExamSlot
 *       already existed as a standalone entity referencing its parent Exam by ID
 *       (not nested inside Exam) — added here for consistency with the rest of the
 *       repository layer.
 * Important Assumptions: ExamSlot IDs are unique. Data is transient and resets on
 *       application restart.
 */
@Repository
public class ExamSlotRepository {
    private final Map<String, ExamSlot> examSlots = new ConcurrentHashMap<>();

    /**
     * Saves an exam slot to the repository.
     * @param examSlot The exam slot to save.
     * @return The saved exam slot.
     */
    public ExamSlot save(ExamSlot examSlot) {
        examSlots.put(examSlot.getId(), examSlot);
        return examSlot;
    }

    /**
     * Finds an exam slot by its ID.
     * @param id The ID of the exam slot to find.
     * @return An Optional containing the exam slot if found, empty otherwise.
     */
    public Optional<ExamSlot> findById(String id) {
        return Optional.ofNullable(examSlots.get(id));
    }

    /**
     * Deletes an exam slot by its ID.
     * @param id The ID of the exam slot to delete.
     */
    public void deleteById(String id) {
        examSlots.remove(id);
    }

    /**
     * Checks if an exam slot with the given ID exists.
     * @param id The ID of the exam slot to check.
     * @return true if the exam slot exists, false otherwise.
     */
    public boolean existsById(String id) {
        return examSlots.containsKey(id);
    }

    /**
     * Finds all exam slots belonging to a given exam.
     * @param examId The ID of the parent exam.
     * @return A list of exam slots for the specified exam.
     */
    public List<ExamSlot> findByExamId(String examId) {
        return examSlots.values().stream()
                .filter(slot -> slot.getExamId().equals(examId))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all exam slots in the repository.
     * @return A collection of all exam slots.
     */
    public Collection<ExamSlot> findAll() {
        return examSlots.values();
    }
}
