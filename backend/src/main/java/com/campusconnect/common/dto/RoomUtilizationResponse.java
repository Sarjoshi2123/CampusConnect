package com.campusconnect.common.dto;

public class RoomUtilizationResponse {
    private String examSlotId;
    private String proctoringRoomId;
    private int capacity;
    private int filled;
    private int available;

    public RoomUtilizationResponse(String examSlotId, String proctoringRoomId, int capacity, int filled, int available) {
        this.examSlotId = examSlotId;
        this.proctoringRoomId = proctoringRoomId;
        this.capacity = capacity;
        this.filled = filled;
        this.available = available;
    }

    public String getExamSlotId() {
        return examSlotId;
    }

    public String getProctoringRoomId() {
        return proctoringRoomId;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getFilled() {
        return filled;
    }

    public int getAvailable() {
        return available;
    }
}
