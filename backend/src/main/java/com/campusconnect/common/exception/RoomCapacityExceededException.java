package com.campusconnect.common.exception;

/**
 * Purpose: Signals that no proctoring room with available capacity could be found
 *          for an exam slot at the time of registration or reschedule.
 * Role: Thrown by RegistrationService's room allocation step; mapped to HTTP 400
 *       by GlobalExceptionHandler.
 */
public class RoomCapacityExceededException extends RuntimeException {
    public RoomCapacityExceededException(String message) {
        super(message);
    }
}
