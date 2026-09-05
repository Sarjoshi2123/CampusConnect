package com.campusconnect.common.exception;

/**
 * Purpose: Signals that a reschedule was requested into a slot belonging to a
 *          different exam than the one the student is currently registered for.
 * Role: Thrown by RegistrationService.reschedule() before any conflict/capacity
 *       check runs on the new slot; mapped to HTTP 400 by GlobalExceptionHandler.
 * Important Assumptions: A reschedule may only move a student between slots of
 *       the SAME exam — moving them onto a different exam entirely is treated as
 *       a new registration decision, not a reschedule.
 */
public class DifferentExamRescheduleException extends RuntimeException {
    public DifferentExamRescheduleException(String message) {
        super(message);
    }
}
