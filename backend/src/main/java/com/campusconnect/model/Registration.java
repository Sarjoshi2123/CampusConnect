package com.campusconnect.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Purpose: Represents a student's registration for an exam slot in a specific proctoring room.
 * Role: Domain entity linking a student to an exam slot and room, tracking registration status.
 * Important Assumptions: Registration ID is unique. Student can only have one active registration per exam slot.
 */
public class Registration {
    private String id;
    private String studentId;
    private String examId;
    private String examSlotId;
    private String proctoringRoomId;
    private LocalDateTime registrationTime;
    private Status status;

    public enum Status {
        REGISTERED,
        CHECKED_IN,
        NO_SHOW,
        CANCELLED // Used for reschedule mechanism where old registration is effectively cancelled
    }

    public Registration(String id, String studentId, String examId, String examSlotId, String proctoringRoomId, LocalDateTime registrationTime, Status status) {
        this.id = id;
        this.studentId = studentId;
        this.examId = examId;
        this.examSlotId = examSlotId;
        this.proctoringRoomId = proctoringRoomId;
        this.registrationTime = registrationTime;
        this.status = status;
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

    public LocalDateTime getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(LocalDateTime registrationTime) {
        this.registrationTime = registrationTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Registration that = (Registration) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
