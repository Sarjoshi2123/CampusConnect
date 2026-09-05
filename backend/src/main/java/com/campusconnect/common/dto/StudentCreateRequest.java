package com.campusconnect.common.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Purpose: DTO for creating a new Student.
 * Role: Carries request data from the client to the service layer for student creation.
 * Important Assumptions: Student name is required and non-blank.
 */
public class StudentCreateRequest {

    @NotBlank(message = "Student name must not be blank")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
