package com.campusconnect.common.dto;

public class AdmitTicketResponse {
    private String id;
    private String studentId;
    private String examId;
    private String examSlotId;
    private String proctoringRoomId;
    private String registrationId;
    private boolean used;

    public AdmitTicketResponse(String id, String studentId, String examId, String examSlotId,
                                String proctoringRoomId, String registrationId, boolean used) {
        this.id = id;
        this.studentId = studentId;
        this.examId = examId;
        this.examSlotId = examSlotId;
        this.proctoringRoomId = proctoringRoomId;
        this.registrationId = registrationId;
        this.used = used;
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

    public String getRegistrationId() {
        return registrationId;
    }

    public boolean isUsed() {
        return used;
    }
}
