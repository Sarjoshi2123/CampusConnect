package com.campusconnect.common.dto;

/**
 * Purpose: Read-model DTO reporting how full a single proctoring room is for a
 *          given exam slot.
 * Role: Returned (as a list, one entry per room) by the room utilization query
 *       endpoint, so callers can see filled vs. available capacity per slot.
 */
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
