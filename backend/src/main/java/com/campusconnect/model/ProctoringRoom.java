package com.campusconnect.model;

import java.util.Objects;

/**
 * Purpose: Represents a proctoring room allocated for a specific exam slot.
 * Role: Domain entity holding room capacity and current occupancy information.
 * Important Assumptions: ProctoringRoom ID is unique. Belongs to an ExamSlot.
 */
public class ProctoringRoom {
    private String id;
    private String examSlotId;
    private int capacity;
    private int currentOccupancy;

    public ProctoringRoom(String id, String examSlotId, int capacity) {
        this.id = id;
        this.examSlotId = examSlotId;
        this.capacity = capacity;
        this.currentOccupancy = 0; // Initialize with no occupancy
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExamSlotId() {
        return examSlotId;
    }

    public void setExamSlotId(String examSlotId) {
        this.examSlotId = examSlotId;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCurrentOccupancy() {
        return currentOccupancy;
    }

    public void setCurrentOccupancy(int currentOccupancy) {
        this.currentOccupancy = currentOccupancy;
    }

    public int getAvailableCapacity() {
        return capacity - currentOccupancy;
    }

    public boolean isFull() {
        return currentOccupancy >= capacity;
    }

    public void incrementOccupancy() {
        if (!isFull()) {
            this.currentOccupancy++;
        } else {
            throw new IllegalStateException("Room is already at full capacity.");
        }
    }

    public void decrementOccupancy() {
        if (this.currentOccupancy > 0) {
            this.currentOccupancy--;
        } else {
            throw new IllegalStateException("Room is already empty.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProctoringRoom that = (ProctoringRoom) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
