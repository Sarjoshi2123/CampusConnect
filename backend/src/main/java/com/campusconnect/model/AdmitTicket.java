package com.campusconnect.model;

import java.util.Objects;

public class AdmitTicket {
    private String id;
    private String studentId;
    private String examId;
    private String examSlotId;
    private String proctoringRoomId;
    private String registrationId;
    private boolean used;

    public AdmitTicket(String id, String studentId, String examId, String examSlotId, String proctoringRoomId, String registrationId) {
        this.id = id;
        this.studentId = studentId;
        this.examId = examId;
        this.examSlotId = examSlotId;
        this.proctoringRoomId = proctoringRoomId;
        this.registrationId = registrationId;
        this.used = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public String getExamSlotId() {
        return examSlotId;
    }

    public void setExamSlotId(String examSlotId) {
        this.examSlotId = examSlotId;
    }

    public String getProctoringRoomId() {
        return proctoringRoomId;
    }

    public void setProctoringRoomId(String proctoringRoomId) {
        this.proctoringRoomId = proctoringRoomId;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdmitTicket that = (AdmitTicket) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
