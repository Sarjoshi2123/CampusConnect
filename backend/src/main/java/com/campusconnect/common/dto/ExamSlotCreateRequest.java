package com.campusconnect.common.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Purpose: DTO for creating a new ExamSlot.
 * Role: Carries request data from the client to the service layer for exam slot creation.
 * Important Assumptions: Date, start time, and duration are required.
 */
public class ExamSlotCreateRequest {
    private LocalDate date;
    private LocalTime startTime;
    private int durationMinutes;

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}
