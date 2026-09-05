package com.campusconnect.observer;

/**
 * Observer for tracking audit trails of all system state changes.
 */
public class AuditLogObserver implements AlertObserver {
    @Override
    public void onRegistrationSuccess(String studentId, String examId, String slotId, String roomId) {
        System.out.printf("  📋 [AUDIT LOG] Registered Student '%s' -> Exam '%s' | Slot '%s' | Room '%s'\n",
                studentId, examId, slotId, roomId);
    }

    @Override
    public void onRescheduleSuccess(String studentId, String examId, String oldSlotId, String newSlotId, String newRoomId) {
        System.out.printf("  📋 [AUDIT LOG] Rescheduled Student '%s' -> Exam '%s' from Slot '%s' to Slot '%s' (Room '%s')\n",
                studentId, examId, oldSlotId, newSlotId, newRoomId);
    }

    @Override
    public void onSchedulingConflictAlert(String studentId, String requestedSlotId, String reason) {
        System.out.printf("  📋 [AUDIT LOG] Conflict Detected for Student '%s' requesting Slot '%s': %s\n",
                studentId, requestedSlotId, reason);
    }

    @Override
    public void onCapacityAlert(String slotId, String roomId, int filled, int maxCapacity) {
        System.out.printf("  📋 [AUDIT LOG] Capacity Update for Room '%s' in Slot '%s': %d/%d seats filled\n",
                roomId, slotId, filled, maxCapacity);
    }

    @Override
    public void onNoShowRecorded(String studentId, String examId, String slotId) {
        System.out.printf("  📋 [AUDIT LOG] No-Show Recorded: Student '%s' | Exam '%s' | Slot '%s'\n",
                studentId, examId, slotId);
    }

    @Override
    public void onCheckInSuccess(String ticketId, String studentId, String slotId, String roomId) {
        System.out.printf("  📋 [AUDIT LOG] Check-in Successful: Ticket '%s' | Student '%s' | Slot '%s' | Room '%s'\n",
                ticketId, studentId, slotId, roomId);
    }
}
