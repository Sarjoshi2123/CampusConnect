package com.campusconnect.repository;

import com.campusconnect.model.ExamSlot;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class ExamSlotRepository {
    private final Map<String, ExamSlot> examSlots = new ConcurrentHashMap<>();

    public ExamSlot save(ExamSlot examSlot) {
        examSlots.put(examSlot.getId(), examSlot);
        return examSlot;
    }

    public Optional<ExamSlot> findById(String id) {
        return Optional.ofNullable(examSlots.get(id));
    }

    public void deleteById(String id) {
        examSlots.remove(id);
    }

    public boolean existsById(String id) {
        return examSlots.containsKey(id);
    }

    public List<ExamSlot> findByExamId(String examId) {
        return examSlots.values().stream()
                .filter(slot -> slot.getExamId().equals(examId))
                .collect(Collectors.toList());
    }

    public Collection<ExamSlot> findAll() {
        return examSlots.values();
    }
}
