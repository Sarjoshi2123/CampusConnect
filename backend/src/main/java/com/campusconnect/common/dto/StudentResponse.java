package com.campusconnect.common.dto;

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
