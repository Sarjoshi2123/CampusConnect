package com.campusconnect.observer;

/**
 * Observer interface for monitoring system events, capacity thresholds, and conflict alerts.
 */
public interface AlertObserver {
    void onRegistrationSuccess(String studentId, String examId, String slotId, String roomId);
    void onRescheduleSuccess(String studentId, String examId, String oldSlotId, String newSlotId, String newRoomId);
    void onSchedulingConflictAlert(String studentId, String requestedSlotId, String reason);
    void onCapacityAlert(String slotId, String roomId, int filled, int maxCapacity);
    void onNoShowRecorded(String studentId, String examId, String slotId);
    void onCheckInSuccess(String ticketId, String studentId, String slotId, String roomId);
}
