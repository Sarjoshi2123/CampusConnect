package com.campusconnect.common.dto;

import jakarta.validation.constraints.NotBlank;

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
