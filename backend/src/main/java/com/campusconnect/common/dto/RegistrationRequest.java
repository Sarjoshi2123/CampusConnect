package com.campusconnect.common.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Purpose: DTO for registering a student into an exam slot.
 * Role: Carries request data from the client to RegistrationService#register.
 * Important Assumptions: The exam is derived server-side from the exam slot, so
 *       only the student and target slot need to be supplied.
 */
public class RegistrationRequest {

    @NotBlank(message = "studentId must not be blank")
    private String studentId;

    @NotBlank(message = "examSlotId must not be blank")
    private String examSlotId;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getExamSlotId() {
        return examSlotId;
    }

    public void setExamSlotId(String examSlotId) {
        this.examSlotId = examSlotId;
    }
}
