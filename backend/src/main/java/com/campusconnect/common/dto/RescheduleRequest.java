package com.campusconnect.common.dto;

import jakarta.validation.constraints.NotBlank;

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
