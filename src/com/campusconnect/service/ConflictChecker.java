package com.campusconnect.service;

import com.campusconnect.model.ExamSlot;
import com.campusconnect.model.Registration;
import com.campusconnect.model.RegistrationStatus;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Service dedicated to checking time overlap conflicts for student exam registrations.
 * Handles cross-exam and cross-university slot conflict validation.
 */
public class ConflictChecker {

    /**
     * Checks if targetSlot overlaps with any active (CONFIRMED or CHECKED_IN) registration of the student.
     *
     * @param studentId ID of the student being checked
     * @param targetSlot The slot candidate for registration or rescheduling
     * @param allSlots Map of all available exam slots by slotId
     * @param studentRegistrations Active registrations of the student
     * @param excludeRegistrationId Optional registration ID to ignore (used during rescheduling)
     * @return Optional containing the conflicting ExamSlot if an overlap exists, empty otherwise
     */
    public Optional<ExamSlot> findConflictingSlot(String studentId,
                                                  ExamSlot targetSlot,
                                                  Map<String, ExamSlot> allSlots,
                                                  Collection<Registration> studentRegistrations,
                                                  String excludeRegistrationId) {
        if (targetSlot == null || studentRegistrations == null) {
            return Optional.empty();
        }

        for (Registration reg : studentRegistrations) {
            // Ignore cancelled, no-show, or explicitly excluded registrations (e.g., the slot being replaced)
            if (reg.getStatus() == RegistrationStatus.CANCELLED || reg.getStatus() == RegistrationStatus.NO_SHOW) {
                continue;
            }
            if (excludeRegistrationId != null && reg.getRegistrationId().equals(excludeRegistrationId)) {
                continue;
            }

            ExamSlot existingSlot = allSlots.get(reg.getSlotId());
            if (existingSlot != null && targetSlot.overlapsWith(existingSlot)) {
                return Optional.of(existingSlot);
            }
        }

        return Optional.empty();
    }
}
