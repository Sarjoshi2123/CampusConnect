package com.campusconnect.common.dto;

/**
 * Purpose: DTO for creating a new ProctoringRoom.
 * Role: Carries request data from the client to the service layer for proctoring room creation.
 * Important Assumptions: Capacity is required.
 */
public class ProctoringRoomCreateRequest {
    private int capacity;

    // Getters and Setters
    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
