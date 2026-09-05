package com.campusconnect.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a scheduled time slot for an exam, containing one or more proctoring rooms.
 */
public class ExamSlot {
    private final String slotId;
    private final String examId;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final LocalDateTime checkInOpenTime;
    private final LocalDateTime checkInCloseTime;
    private final List<ProctoringRoom> proctoringRooms;

    public ExamSlot(String slotId, String examId, LocalDateTime startTime, LocalDateTime endTime,
                    LocalDateTime checkInOpenTime, LocalDateTime checkInCloseTime) {
        if (slotId == null || slotId.trim().isEmpty()) {
            throw new IllegalArgumentException("Slot ID cannot be null or empty.");
        }
        if (examId == null || examId.trim().isEmpty()) {
            throw new IllegalArgumentException("Exam ID cannot be null or empty.");
        }
        if (startTime == null || endTime == null || !endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("Invalid slot duration. End time must be after start time.");
        }
        this.slotId = slotId;
        this.examId = examId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.checkInOpenTime = checkInOpenTime != null ? checkInOpenTime : startTime.minusMinutes(15);
        this.checkInCloseTime = checkInCloseTime != null ? checkInCloseTime : startTime;
        this.proctoringRooms = new ArrayList<>();
    }

    public ExamSlot(String slotId, String examId, LocalDateTime startTime, LocalDateTime endTime) {
        this(slotId, examId, startTime, endTime, startTime.minusMinutes(15), startTime);
    }

    public String getSlotId() {
        return slotId;
    }

    public String getExamId() {
        return examId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public LocalDateTime getCheckInOpenTime() {
        return checkInOpenTime;
    }

    public LocalDateTime getCheckInCloseTime() {
        return checkInCloseTime;
    }

    public void addProctoringRoom(ProctoringRoom room) {
        if (room != null) {
            proctoringRooms.add(room);
        }
    }

    public List<ProctoringRoom> getProctoringRooms() {
        return Collections.unmodifiableList(proctoringRooms);
    }

    public int getTotalCapacity() {
        return proctoringRooms.stream().mapToInt(ProctoringRoom::getCapacity).sum();
    }

    public int getFilledCapacity() {
        return proctoringRooms.stream().mapToInt(ProctoringRoom::getFilledCapacity).sum();
    }

    public int getAvailableCapacity() {
        return proctoringRooms.stream().mapToInt(ProctoringRoom::getAvailableCapacity).sum();
    }

    /**
     * Checks if this slot's time window overlaps with another slot.
     */
    public boolean overlapsWith(ExamSlot other) {
        if (other == null) return false;
        return this.startTime.isBefore(other.endTime) && other.startTime.isBefore(this.endTime);
    }

    /**
     * Checks if the check-in window has opened relative to a reference timestamp.
     */
    public boolean isCheckInWindowOpened(LocalDateTime currentTime) {
        return !currentTime.isBefore(checkInOpenTime);
    }

    /**
     * Checks if the check-in window has closed relative to a reference timestamp.
     */
    public boolean isCheckInWindowClosed(LocalDateTime currentTime) {
        return currentTime.isAfter(checkInCloseTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExamSlot examSlot = (ExamSlot) o;
        return Objects.equals(slotId, examSlot.slotId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slotId);
    }

    @Override
    public String toString() {
        return "ExamSlot{" +
                "slotId='" + slotId + '\'' +
                ", examId='" + examId + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", roomsCount=" + proctoringRooms.size() +
                '}';
    }
}
