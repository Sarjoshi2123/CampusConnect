package com.campusconnect.common.dto;

/**
 * Purpose: DTO for creating a new Exam.
 * Role: Carries request data from the client to the service layer for exam creation.
 * Important Assumptions: Exam name and description are required.
 */
public class ExamCreateRequest {
    private String name;
    private String description;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
