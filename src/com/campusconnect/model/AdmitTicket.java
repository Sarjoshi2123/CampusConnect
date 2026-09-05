package com.campusconnect.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Digital admit ticket issued to a student upon successful registration.
 * Required for check-in at the exam.
 */
public class AdmitTicket {
    private final String ticketId;
    private final String registrationId;
    private final String studentId;
    private final String examId;
    private final String slotId;
    private final String roomId;
    private final LocalDateTime issueTime;
    private boolean isUsed;
    private LocalDateTime checkInTime;

    public AdmitTicket(String ticketId, String registrationId, String studentId, String examId, String slotId, String roomId) {
        if (ticketId == null || ticketId.trim().isEmpty()) {
            throw new IllegalArgumentException("Ticket ID cannot be null or empty.");
        }
        this.ticketId = ticketId;
        this.registrationId = registrationId;
        this.studentId = studentId;
        this.examId = examId;
        this.slotId = slotId;
        this.roomId = roomId;
        this.issueTime = LocalDateTime.now();
        this.isUsed = false;
        this.checkInTime = null;
    }

    public String getTicketId() {
        return ticketId;
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

    public String getRoomId() {
        return roomId;
    }

    public LocalDateTime getIssueTime() {
        return issueTime;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void markAsUsed(LocalDateTime checkInTime) {
        this.isUsed = true;
        this.checkInTime = checkInTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdmitTicket ticket = (AdmitTicket) o;
        return Objects.equals(ticketId, ticket.ticketId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticketId);
    }

    @Override
    public String toString() {
        return "AdmitTicket{" +
                "ticketId='" + ticketId + '\'' +
                ", studentId='" + studentId + '\'' +
                ", examId='" + examId + '\'' +
                ", slotId='" + slotId + '\'' +
                ", roomId='" + roomId + '\'' +
                ", isUsed=" + isUsed +
                '}';
    }
}
