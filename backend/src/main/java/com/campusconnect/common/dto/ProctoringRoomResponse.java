package com.campusconnect.common.dto;

/**
 * Purpose: Read-model DTO representing a ProctoringRoom in API responses.
 * Role: Decouples the API contract from the ProctoringRoom domain entity so the
 *       entity is never exposed directly through the REST layer.
 */
public class ProctoringRoomResponse {
    private String id;
    private String examSlotId;
    private int capacity;
    private int currentOccupancy;
    private int availableCapacity;

    public ProctoringRoomResponse(String id, String examSlotId, int capacity,
                                   int currentOccupancy, int availableCapacity) {
        this.id = id;
        this.examSlotId = examSlotId;
        this.capacity = capacity;
        this.currentOccupancy = currentOccupancy;
        this.availableCapacity = availableCapacity;
    }

    public String getId() {
        return id;
    }

    public String getExamSlotId() {
        return examSlotId;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCurrentOccupancy() {
        return currentOccupancy;
    }

    public int getAvailableCapacity() {
        return availableCapacity;
    }
}
