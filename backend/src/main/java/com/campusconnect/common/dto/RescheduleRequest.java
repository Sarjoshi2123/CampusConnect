package com.campusconnect.common.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Purpose: DTO for rescheduling an existing registration to a different exam slot.
 * Role: Carries request data from the client to RegistrationService#reschedule.
 */
public class RescheduleRequest {

    @NotBlank(message = "newExamSlotId must not be blank")
    private String newExamSlotId;

    public String getNewExamSlotId() {
        return newExamSlotId;
    }

    public void setNewExamSlotId(String newExamSlotId) {
        this.newExamSlotId = newExamSlotId;
    }
}
