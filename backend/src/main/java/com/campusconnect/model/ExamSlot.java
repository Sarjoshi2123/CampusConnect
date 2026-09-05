package com.campusconnect.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Purpose: Represents an exam slot in the CampusConnect system.
 * Role: Domain entity holding scheduling information for an exam.
 * Important Assumptions: ExamSlot ID is unique. Belongs to an Exam.
 */
public class ExamSlot {
    private String id;
    private String examId;
    private LocalDate date;
    private LocalTime startTime;
    private int durationMinutes;

    public ExamSlot(String id, String examId, LocalDate date, LocalTime startTime, int durationMinutes) {
        this.id = id;
        this.examId = examId;
        this.date = date;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

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

    /**
     * Calculates the end time of the exam slot.
     * @return LocalTime representing the end time.
     */
    public LocalTime getEndTime() {
        return startTime.plusMinutes(durationMinutes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExamSlot examSlot = (ExamSlot) o;
        return Objects.equals(id, examSlot.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
