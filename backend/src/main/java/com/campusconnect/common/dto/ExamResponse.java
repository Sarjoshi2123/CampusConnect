package com.campusconnect.common.dto;

public class ExamResponse {
    private String id;
    private String name;
    private String description;

    public ExamResponse(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
