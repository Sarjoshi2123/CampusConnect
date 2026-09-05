package com.campusconnect.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a capacity-limited virtual proctoring room allocated for a slot.
 */
public class ProctoringRoom {
    private final String roomId;
    private final String roomName;
    private final int capacity;
    private final Set<String> assignedStudentIds;

    public ProctoringRoom(String roomId, String roomName, int capacity) {
        if (roomId == null || roomId.trim().isEmpty()) {
            throw new IllegalArgumentException("Room ID cannot be null or empty.");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Room capacity must be greater than 0.");
        }
        this.roomId = roomId;
        this.roomName = roomName;
        this.capacity = capacity;
        this.assignedStudentIds = new HashSet<>();
    }

    public String getRoomId() {
        return roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getCapacity() {
        return capacity;
    }

    public synchronized int getFilledCapacity() {
        return assignedStudentIds.size();
    }

    public synchronized int getAvailableCapacity() {
        return capacity - assignedStudentIds.size();
    }

    public synchronized boolean isFull() {
        return assignedStudentIds.size() >= capacity;
    }

    public synchronized double getUtilizationPercentage() {
        if (capacity == 0) return 0.0;
        return ((double) assignedStudentIds.size() / capacity) * 100.0;
    }

    public synchronized boolean assignStudent(String studentId) {
        if (isFull() || assignedStudentIds.contains(studentId)) {
            return false;
        }
        return assignedStudentIds.add(studentId);
    }

    public synchronized boolean removeStudent(String studentId) {
        return assignedStudentIds.remove(studentId);
    }

    public synchronized boolean hasStudent(String studentId) {
        return assignedStudentIds.contains(studentId);
    }

    public synchronized Set<String> getAssignedStudentIds() {
        return Collections.unmodifiableSet(new HashSet<>(assignedStudentIds));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProctoringRoom that = (ProctoringRoom) o;
        return Objects.equals(roomId, that.roomId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId);
    }

    @Override
    public String toString() {
        return "ProctoringRoom{" +
                "roomId='" + roomId + '\'' +
                ", roomName='" + roomName + '\'' +
                ", capacity=" + capacity +
                ", filled=" + getFilledCapacity() +
                '}';
    }
}
