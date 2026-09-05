package com.campusconnect.repository;

import com.campusconnect.model.Student;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class StudentRepository {
    private final Map<String, Student> students = new ConcurrentHashMap<>();

    public Student save(Student student) {
        students.put(student.getId(), student);
        return student;
    }

    public Optional<Student> findById(String id) {
        return Optional.ofNullable(students.get(id));
    }

    public void deleteById(String id) {
        students.remove(id);
    }

    public boolean existsById(String id) {
        return students.containsKey(id);
    }
}
