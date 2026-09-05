package com.campusconnect.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a student's registration for a specific exam slot and proctoring room.
 */
public class Registration {
    private final String registrationId;
    private final String studentId;
    private final String examId;
    private String slotId;
    private String roomId;
    private RegistrationStatus status;
    private final LocalDateTime registrationTime;

    public Registration(String registrationId, String studentId, String examId, String slotId, String roomId) {
        if (registrationId == null || registrationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration ID cannot be null or empty.");
        }
        this.registrationId = registrationId;
        this.studentId = studentId;
        this.examId = examId;
        this.slotId = slotId;
        this.roomId = roomId;
        this.status = RegistrationStatus.CONFIRMED;
        this.registrationTime = LocalDateTime.now();
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getExamId() {
        return examId;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }

    public LocalDateTime getRegistrationTime() {
        return registrationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Registration that = (Registration) o;
        return Objects.equals(registrationId, that.registrationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registrationId);
    }

    @Override
    public String toString() {
        return "Registration{" +
                "registrationId='" + registrationId + '\'' +
                ", studentId='" + studentId + '\'' +
                ", examId='" + examId + '\'' +
                ", slotId='" + slotId + '\'' +
                ", roomId='" + roomId + '\'' +
                ", status=" + status +
                '}';
    }
}
