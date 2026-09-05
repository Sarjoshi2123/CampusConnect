package com.campusconnect.service;

import com.campusconnect.common.exception.ExamSlotNotFoundException;
import com.campusconnect.model.ExamSlot;
import com.campusconnect.model.Registration;
import com.campusconnect.model.Registration.Status;
import com.campusconnect.repository.ExamSlotRepository;
import com.campusconnect.repository.RegistrationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoShowService {

    private final ExamSlotRepository examSlotRepository;
    private final RegistrationRepository registrationRepository;

    public NoShowService(ExamSlotRepository examSlotRepository,
                          RegistrationRepository registrationRepository) {
        this.examSlotRepository = examSlotRepository;
        this.registrationRepository = registrationRepository;
    }

    @Scheduled(fixedRate = 60000)
    public int markNoShowsForClosedSlots() {
        int marked = 0;
        for (ExamSlot slot : examSlotRepository.findAll()) {
            marked += markNoShowsForSlotIfClosed(slot);
        }
        return marked;
    }

    public int markNoShowsForSlot(String examSlotId) {
        ExamSlot slot = examSlotRepository.findById(examSlotId)
                .orElseThrow(() -> new ExamSlotNotFoundException("Exam slot not found: " + examSlotId));
        return markNoShowsForSlotIfClosed(slot);
    }

    private int markNoShowsForSlotIfClosed(ExamSlot slot) {
        LocalDateTime slotEnd = LocalDateTime.of(slot.getDate(), slot.getEndTime());
        if (LocalDateTime.now().isBefore(slotEnd)) {
            return 0;
        }
        List<Registration> stillRegistered = registrationRepository.findByExamSlotId(slot.getId()).stream()
                .filter(r -> r.getStatus() == Status.REGISTERED)
                .collect(Collectors.toList());
        for (Registration registration : stillRegistered) {
            registration.setStatus(Status.NO_SHOW);
            registrationRepository.save(registration);
        }
        return stillRegistered.size();
    }
}
