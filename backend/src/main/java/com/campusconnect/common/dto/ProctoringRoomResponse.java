package com.campusconnect.common.dto;

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
