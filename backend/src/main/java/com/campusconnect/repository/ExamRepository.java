package com.campusconnect.repository;

import com.campusconnect.model.Exam;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ExamRepository {
    private final Map<String, Exam> exams = new ConcurrentHashMap<>();

    public Exam save(Exam exam) {
        exams.put(exam.getId(), exam);
        return exam;
    }

    public Optional<Exam> findById(String id) {
        return Optional.ofNullable(exams.get(id));
    }

    public void deleteById(String id) {
        exams.remove(id);
    }

    public boolean existsById(String id) {
        return exams.containsKey(id);
    }

    public Collection<Exam> findAll() {
        return exams.values();
    }
}
