package com.campusconnect.common.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Purpose: Read-model DTO representing an ExamSlot in API responses.
 * Role: Decouples the API contract from the ExamSlot domain entity so the entity is
 *       never exposed directly through the REST layer.
 */
public class ExamSlotResponse {
    private String id;
    private String examId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int durationMinutes;

    public ExamSlotResponse(String id, String examId, LocalDate date, LocalTime startTime,
                             LocalTime endTime, int durationMinutes) {
        this.id = id;
        this.examId = examId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = durationMinutes;
    }

    public String getId() {
        return id;
    }

    public String getExamId() {
        return examId;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }
}
