package com.campusconnect.repository;

import com.campusconnect.model.Registration;
import com.campusconnect.model.Registration.Status;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class RegistrationRepository {
    private final Map<String, Registration> registrations = new ConcurrentHashMap<>();

    public Registration save(Registration registration) {
        registrations.put(registration.getId(), registration);
        return registration;
    }

    public Optional<Registration> findById(String id) {
        return Optional.ofNullable(registrations.get(id));
    }

    public void deleteById(String id) {
        registrations.remove(id);
    }

    public boolean existsById(String id) {
        return registrations.containsKey(id);
    }

    public List<Registration> findActiveRegistrationsByStudentId(String studentId) {
        return registrations.values().stream()
                .filter(reg -> reg.getStudentId().equals(studentId) && reg.getStatus() == Status.REGISTERED)
                .collect(Collectors.toList());
    }

    public Optional<Registration> findActiveRegistrationByStudentIdAndExamSlotId(String studentId, String examSlotId) {
        return registrations.values().stream()
                .filter(reg -> reg.getStudentId().equals(studentId) &&
                                 reg.getExamSlotId().equals(examSlotId) &&
                                 reg.getStatus() == Status.REGISTERED)
                .findFirst();
    }

    public List<Registration> findAllRegistrationsByStudentId(String studentId) {
        return registrations.values().stream()
                .filter(reg -> reg.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    public List<Registration> findByExamSlotId(String examSlotId) {
        return registrations.values().stream()
                .filter(reg -> reg.getExamSlotId().equals(examSlotId))
                .collect(Collectors.toList());
    }

    public Collection<Registration> findAll() {
        return registrations.values();
    }
}
