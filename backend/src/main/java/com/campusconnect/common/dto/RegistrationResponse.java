package com.campusconnect.common.dto;

import java.time.LocalDateTime;

/**
 * Purpose: Read-model DTO representing a Registration (and the admit ticket issued
 *          alongside it, when one exists) in API responses.
 * Role: Decouples the API contract from the Registration domain entity so the
 *       entity is never exposed directly through the REST layer.
 */
public class RegistrationResponse {
    private String id;
    private String studentId;
    private String examId;
    private String examSlotId;
    private String proctoringRoomId;
    private LocalDateTime registrationTime;
    private String status;
    private String admitTicketId;

    public RegistrationResponse(String id, String studentId, String examId, String examSlotId,
                                 String proctoringRoomId, LocalDateTime registrationTime,
                                 String status, String admitTicketId) {
        this.id = id;
        this.studentId = studentId;
        this.examId = examId;
        this.examSlotId = examSlotId;
        this.proctoringRoomId = proctoringRoomId;
        this.registrationTime = registrationTime;
        this.status = status;
        this.admitTicketId = admitTicketId;
    }

    public String getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getExamId() {
        return examId;
    }

    public String getExamSlotId() {
        return examSlotId;
    }

    public String getProctoringRoomId() {
        return proctoringRoomId;
    }

    public LocalDateTime getRegistrationTime() {
        return registrationTime;
    }

    public String getStatus() {
        return status;
    }

    public String getAdmitTicketId() {
        return admitTicketId;
    }
}
