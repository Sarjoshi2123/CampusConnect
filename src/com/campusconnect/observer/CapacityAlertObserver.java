package com.campusconnect.observer;

/**
 * Observer focused on monitoring and logging room capacity alerts (e.g., full rooms).
 */
public class CapacityAlertObserver implements AlertObserver {
    @Override
    public void onRegistrationSuccess(String studentId, String examId, String slotId, String roomId) {
        // No-op for general registration
    }

    @Override
    public void onRescheduleSuccess(String studentId, String examId, String oldSlotId, String newSlotId, String newRoomId) {
        // No-op for general reschedule
    }

    @Override
    public void onSchedulingConflictAlert(String studentId, String requestedSlotId, String reason) {
        // No-op
    }

    @Override
    public void onCapacityAlert(String slotId, String roomId, int filled, int maxCapacity) {
        System.out.printf("  ⚠️  [ALERT - ROOM CAPACITY] Room '%s' in Slot '%s' has reached FULL capacity (%d/%d students).\n",
                roomId, slotId, filled, maxCapacity);
    }

    @Override
    public void onNoShowRecorded(String studentId, String examId, String slotId) {
        // No-op
    }

    @Override
    public void onCheckInSuccess(String ticketId, String studentId, String slotId, String roomId) {
        // No-op
    }
}
