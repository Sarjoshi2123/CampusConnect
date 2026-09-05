package com.campusconnect.common.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class ExamSlotCreateRequest {
    private LocalDate date;
    private LocalTime startTime;
    private int durationMinutes;

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
