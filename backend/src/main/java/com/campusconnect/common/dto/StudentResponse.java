package com.campusconnect.common.dto;

/**
 * Purpose: Read-model DTO representing a Student in API responses.
 * Role: Decouples the API contract from the Student domain entity so the entity is
 *       never exposed directly through the REST layer.
 */
public class StudentResponse {
    private String id;
    private String name;

    public StudentResponse(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
