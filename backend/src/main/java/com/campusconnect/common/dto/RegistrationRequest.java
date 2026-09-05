package com.campusconnect.common.dto;

import jakarta.validation.constraints.NotBlank;

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
