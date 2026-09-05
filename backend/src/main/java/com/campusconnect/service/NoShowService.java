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

/**
 * Purpose: Marks students as NO_SHOW once the check-in window for their
 *          registered exam slot has closed without a check-in.
 * Role: Runs on a fixed schedule (see CampusConnectApplication's @EnableScheduling)
 *       sweeping every exam slot, and also exposes a per-slot method that
 *       controllers/tests can call directly for on-demand or deterministic
 *       processing without waiting on the scheduler.
 * Important Assumptions: The check-in window for a slot is [slot start, slot end);
 *       it is considered "closed" once now() is at or after the slot's end time
 *       (start + durationMinutes). Only registrations still in REGISTERED status
 *       are affected — anyone already CHECKED_IN, or a registration already
 *       CANCELLED by a reschedule, is left untouched. Marking a registration
 *       NO_SHOW does NOT free its room seat: the seat was genuinely reserved and
 *       consumed for that (now past) slot, so room-utilization queries continue to
 *       reflect true historical occupancy rather than showing a slot that already
 *       happened as having spare capacity.
 */
@Service
public class NoShowService {

    private final ExamSlotRepository examSlotRepository;
    private final RegistrationRepository registrationRepository;

    public NoShowService(ExamSlotRepository examSlotRepository,
                          RegistrationRepository registrationRepository) {
        this.examSlotRepository = examSlotRepository;
        this.registrationRepository = registrationRepository;
    }

    /**
     * Sweeps every exam slot whose check-in window has closed and marks any
     * still-REGISTERED registration for that slot as NO_SHOW.
     * @return the total number of registrations marked NO_SHOW in this sweep.
     */
    @Scheduled(fixedRate = 60000)
    public int markNoShowsForClosedSlots() {
        int marked = 0;
        for (ExamSlot slot : examSlotRepository.findAll()) {
            marked += markNoShowsForSlotIfClosed(slot);
        }
        return marked;
    }

    /**
     * Processes a single exam slot on demand: if its check-in window has closed,
     * marks any still-REGISTERED registration for it as NO_SHOW. If the window
     * has not closed yet, this is a no-op.
     * @param examSlotId the exam slot to process.
     * @return the number of registrations marked NO_SHOW.
     */
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
