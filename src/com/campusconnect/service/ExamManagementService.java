package com.campusconnect.service;

import com.campusconnect.exception.*;
import com.campusconnect.model.*;
import com.campusconnect.observer.AlertObserver;
import com.campusconnect.strategy.FirstFitAllocationStrategy;
import com.campusconnect.strategy.RoomAllocationStrategy;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Core Facade Service managing CampusConnect online exam registrations,
 * slot scheduling conflicts, proctoring room allocations, tickets, check-in, and no-shows.
 */
public class ExamManagementService {

    private final Map<String, Student> students = new ConcurrentHashMap<>();
    private final Map<String, Exam> exams = new ConcurrentHashMap<>();
    private final Map<String, ExamSlot> slots = new ConcurrentHashMap<>();
    private final Map<String, Registration> registrations = new ConcurrentHashMap<>();
    private final Map<String, AdmitTicket> admitTickets = new ConcurrentHashMap<>();
    // Map ticketId -> RegistrationId
    private final Map<String, String> ticketToRegistrationMap = new ConcurrentHashMap<>();

    private final ConflictChecker conflictChecker;
    private RoomAllocationStrategy allocationStrategy;
    private final List<AlertObserver> observers = new ArrayList<>();

    public ExamManagementService() {
        this.conflictChecker = new ConflictChecker();
        this.allocationStrategy = new FirstFitAllocationStrategy();
    }

    public void setAllocationStrategy(RoomAllocationStrategy strategy) {
        if (strategy != null) {
            this.allocationStrategy = strategy;
        }
    }

    public void addObserver(AlertObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    // --- Entity Registration Methods ---

    public void registerStudent(Student student) {
        students.put(student.getId(), student);
    }

    public void addExam(Exam exam) {
        exams.put(exam.getId(), exam);
    }

    public void addExamSlot(ExamSlot slot) {
        slots.put(slot.getSlotId(), slot);
    }

    public Student getStudent(String studentId) {
        return students.get(studentId);
    }

    public Exam getExam(String examId) {
        return exams.get(examId);
    }

    public ExamSlot getExamSlot(String slotId) {
        return slots.get(slotId);
    }

    public Registration getRegistration(String registrationId) {
        return registrations.get(registrationId);
    }

    public AdmitTicket getAdmitTicket(String ticketId) {
        return admitTickets.get(ticketId);
    }

    // --- Core Operations ---

    /**
     * 1. Register a student for an exam slot.
     * Performs conflict check across all exams/universities, allocates a proctoring room via strategy,
     * and issues a digital admit ticket.
     */
    public synchronized AdmitTicket registerStudentForExam(String studentId, String examId, String slotId) {
        Student student = students.get(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student not found: " + studentId);
        }

        Exam exam = exams.get(examId);
        if (exam == null) {
            throw new IllegalArgumentException("Exam not found: " + examId);
        }

        ExamSlot targetSlot = slots.get(slotId);
        if (targetSlot == null) {
            throw new IllegalArgumentException("Exam slot not found: " + slotId);
        }

        if (!targetSlot.getExamId().equals(examId)) {
            throw new IllegalArgumentException("Slot '" + slotId + "' does not belong to Exam '" + examId + "'.");
        }

        // Fetch student's existing registrations
        List<Registration> studentRegs = getStudentRegistrations(studentId);

        // 4. Conflict Check
        Optional<ExamSlot> conflictSlotOpt = conflictChecker.findConflictingSlot(
                studentId, targetSlot, slots, studentRegs, null
        );

        if (conflictSlotOpt.isPresent()) {
            ExamSlot conflictingSlot = conflictSlotOpt.get();
            String message = String.format(
                    "Registration rejected: Student '%s' has scheduling conflict with already registered Slot '%s' (%s to %s)",
                    studentId, conflictingSlot.getSlotId(), conflictingSlot.getStartTime(), conflictingSlot.getEndTime()
            );
            notifyConflictAlert(studentId, slotId, message);
            throw new SchedulingConflictException(message);
        }

        // 3. Room Allocation via Strategy
        Optional<ProctoringRoom> selectedRoomOpt = allocationStrategy.selectRoom(targetSlot.getProctoringRooms());
        if (selectedRoomOpt.isEmpty()) {
            throw new CapacityExceededException(
                    String.format("Registration rejected: All proctoring rooms for Slot '%s' are at maximum capacity.", slotId)
            );
        }

        ProctoringRoom room = selectedRoomOpt.get();
        boolean assigned = room.assignStudent(studentId);
        if (!assigned) {
            throw new CapacityExceededException("Failed to assign student to room '" + room.getRoomId() + "'.");
        }

        // Check if room reached full capacity to raise alert
        if (room.isFull()) {
            notifyCapacityAlert(slotId, room.getRoomId(), room.getFilledCapacity(), room.getCapacity());
        }

        // Create Registration record
        String regId = "REG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Registration registration = new Registration(regId, studentId, examId, slotId, room.getRoomId());
        registrations.put(regId, registration);

        // 6. Admit Ticket Generation
        String ticketId = "TICKET-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        AdmitTicket ticket = new AdmitTicket(ticketId, regId, studentId, examId, slotId, room.getRoomId());
        admitTickets.put(ticketId, ticket);
        ticketToRegistrationMap.put(regId, ticketId);

        notifyRegistrationSuccess(studentId, examId, slotId, room.getRoomId());
        return ticket;
    }

    /**
     * 5. Reschedule Request
     * Moves a student to a different slot of the SAME exam, subject to capacity and conflict checks.
     * Safely frees prior room seat ONLY once new slot is confirmed.
     */
    public synchronized AdmitTicket rescheduleStudent(String registrationId, String newSlotId, LocalDateTime referenceTime) {
        Registration reg = registrations.get(registrationId);
        if (reg == null) {
            throw new IllegalArgumentException("Registration not found: " + registrationId);
        }

        if (reg.getStatus() != RegistrationStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot reschedule registration with status: " + reg.getStatus());
        }

        ExamSlot currentSlot = slots.get(reg.getSlotId());
        if (currentSlot == null) {
            throw new IllegalStateException("Current slot not found for registration: " + reg.getSlotId());
        }

        // Business Edge Case 5: Reject if current slot's check-in window has already opened
        if (currentSlot.isCheckInWindowOpened(referenceTime)) {
            throw new RescheduleWindowClosedException(String.format(
                    "Reschedule rejected: Check-in window for current Slot '%s' has already opened at %s.",
                    currentSlot.getSlotId(), currentSlot.getCheckInOpenTime()
            ));
        }

        ExamSlot newSlot = slots.get(newSlotId);
        if (newSlot == null) {
            throw new IllegalArgumentException("New slot not found: " + newSlotId);
        }

        if (!newSlot.getExamId().equals(reg.getExamId())) {
            throw new IllegalArgumentException(String.format(
                    "Reschedule rejected: New slot '%s' belongs to Exam '%s', but registration is for Exam '%s'.",
                    newSlotId, newSlot.getExamId(), reg.getExamId()
            ));
        }

        if (newSlot.getSlotId().equals(currentSlot.getSlotId())) {
            throw new IllegalArgumentException("Student is already registered in slot: " + newSlotId);
        }

        // Business Edge Case 3: Check if new slot creates a conflict with student's OTHER registrations
        List<Registration> studentRegs = getStudentRegistrations(reg.getStudentId());
        Optional<ExamSlot> conflictSlotOpt = conflictChecker.findConflictingSlot(
                reg.getStudentId(), newSlot, slots, studentRegs, registrationId
        );

        if (conflictSlotOpt.isPresent()) {
            ExamSlot conflictingSlot = conflictSlotOpt.get();
            String message = String.format(
                    "Reschedule rejected: Requested new Slot '%s' conflicts with student's existing registered Slot '%s'",
                    newSlotId, conflictingSlot.getSlotId()
            );
            notifyConflictAlert(reg.getStudentId(), newSlotId, message);
            throw new SchedulingConflictException(message);
        }

        // Business Edge Case 2: Check room capacity in new slot via Strategy
        Optional<ProctoringRoom> newRoomOpt = allocationStrategy.selectRoom(newSlot.getProctoringRooms());
        if (newRoomOpt.isEmpty()) {
            throw new CapacityExceededException(
                    String.format("Reschedule rejected: All proctoring rooms in target Slot '%s' are at full capacity.", newSlotId)
            );
        }

        ProctoringRoom newRoom = newRoomOpt.get();

        // Atomic Seat Transfer: Confirm new seat FIRST before releasing old seat
        boolean newSeatAssigned = newRoom.assignStudent(reg.getStudentId());
        if (!newSeatAssigned) {
            throw new CapacityExceededException("Failed to reserve seat in new room '" + newRoom.getRoomId() + "'.");
        }

        // Release prior room seat
        ProctoringRoom oldRoom = currentSlot.getProctoringRooms().stream()
                .filter(r -> r.getRoomId().equals(reg.getRoomId()))
                .findFirst()
                .orElse(null);
        if (oldRoom != null) {
            oldRoom.removeStudent(reg.getStudentId());
        }

        String oldSlotId = reg.getSlotId();

        // Update registration record
        reg.setSlotId(newSlotId);
        reg.setRoomId(newRoom.getRoomId());

        if (newRoom.isFull()) {
            notifyCapacityAlert(newSlotId, newRoom.getRoomId(), newRoom.getFilledCapacity(), newRoom.getCapacity());
        }

        // Issue new updated Admit Ticket
        String oldTicketId = ticketToRegistrationMap.get(registrationId);
        if (oldTicketId != null) {
            admitTickets.remove(oldTicketId);
        }

        String newTicketId = "TICKET-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        AdmitTicket newTicket = new AdmitTicket(newTicketId, registrationId, reg.getStudentId(), reg.getExamId(), newSlotId, newRoom.getRoomId());
        admitTickets.put(newTicketId, newTicket);
        ticketToRegistrationMap.put(registrationId, newTicketId);

        notifyRescheduleSuccess(reg.getStudentId(), reg.getExamId(), oldSlotId, newSlotId, newRoom.getRoomId());
        return newTicket;
    }

    /**
     * 6. Admit Ticket Generation & Check-in Verification
     * Validates ticket presence, single-use restriction, and check-in time window.
     */
    public synchronized void checkInWithTicket(String ticketId, LocalDateTime checkInTime) {
        AdmitTicket ticket = admitTickets.get(ticketId);
        if (ticket == null) {
            throw new InvalidTicketException("Check-in failed: Admit ticket '" + ticketId + "' does not exist.");
        }

        // Business Edge Case 4: Invalid or already-used admit ticket
        if (ticket.isUsed()) {
            throw new InvalidTicketException(String.format(
                    "Check-in failed: Admit ticket '%s' has ALREADY been used at %s.",
                    ticketId, ticket.getCheckInTime()
            ));
        }

        Registration reg = registrations.get(ticket.getRegistrationId());
        if (reg == null || reg.getStatus() != RegistrationStatus.CONFIRMED) {
            throw new InvalidTicketException("Check-in failed: Registration is not active (Status: " +
                    (reg != null ? reg.getStatus() : "null") + ").");
        }

        ExamSlot slot = slots.get(ticket.getSlotId());
        if (slot == null) {
            throw new InvalidTicketException("Check-in failed: Slot not found.");
        }

        // Check-in window validation
        if (checkInTime.isBefore(slot.getCheckInOpenTime())) {
            throw new InvalidTicketException(String.format(
                    "Check-in failed: Check-in window for Slot '%s' has not opened yet (Opens at: %s).",
                    slot.getSlotId(), slot.getCheckInOpenTime()
            ));
        }

        if (checkInTime.isAfter(slot.getCheckInCloseTime())) {
            throw new InvalidTicketException(String.format(
                    "Check-in failed: Check-in window for Slot '%s' closed at %s.",
                    slot.getSlotId(), slot.getCheckInCloseTime()
            ));
        }

        // Process successful check-in
        ticket.markAsUsed(checkInTime);
        reg.setStatus(RegistrationStatus.CHECKED_IN);

        notifyCheckInSuccess(ticketId, ticket.getStudentId(), ticket.getSlotId(), ticket.getRoomId());
    }

    /**
     * 7. Room Utilization Query
     * Displays total, filled, and available capacity breakdown for a given slot.
     */
    public Map<String, Object> getRoomUtilization(String slotId) {
        ExamSlot slot = slots.get(slotId);
        if (slot == null) {
            throw new IllegalArgumentException("Slot not found: " + slotId);
        }

        Map<String, Object> utilization = new LinkedHashMap<>();
        utilization.put("slotId", slotId);
        utilization.put("examId", slot.getExamId());
        utilization.put("startTime", slot.getStartTime());
        utilization.put("endTime", slot.getEndTime());
        utilization.put("totalCapacity", slot.getTotalCapacity());
        utilization.put("filledCapacity", slot.getFilledCapacity());
        utilization.put("availableCapacity", slot.getAvailableCapacity());

        List<Map<String, Object>> roomDetails = new ArrayList<>();
        for (ProctoringRoom room : slot.getProctoringRooms()) {
            Map<String, Object> rMap = new LinkedHashMap<>();
            rMap.put("roomId", room.getRoomId());
            rMap.put("roomName", room.getRoomName());
            rMap.put("capacity", room.getCapacity());
            rMap.put("filled", room.getFilledCapacity());
            rMap.put("available", room.getAvailableCapacity());
            rMap.put("isFull", room.isFull());
            roomDetails.add(rMap);
        }
        utilization.put("rooms", roomDetails);

        return utilization;
    }

    /**
     * 8. No-Show Handling
     * Marks registered students as NO_SHOW if their slot's check-in window closed without checking in.
     */
    public synchronized int processNoShows(String slotId, LocalDateTime referenceTime) {
        ExamSlot slot = slots.get(slotId);
        if (slot == null) {
            throw new IllegalArgumentException("Slot not found: " + slotId);
        }

        if (!slot.isCheckInWindowClosed(referenceTime)) {
            System.out.printf("  ℹ️ Check-in window for Slot '%s' is still open or active. No-show evaluation deferred.\n", slotId);
            return 0;
        }

        int noShowCount = 0;
        for (Registration reg : registrations.values()) {
            if (reg.getSlotId().equals(slotId) && reg.getStatus() == RegistrationStatus.CONFIRMED) {
                reg.setStatus(RegistrationStatus.NO_SHOW);
                noShowCount++;
                notifyNoShow(reg.getStudentId(), reg.getExamId(), slotId);
            }
        }
        return noShowCount;
    }

    // --- Helper Methods ---

    public List<Registration> getStudentRegistrations(String studentId) {
        List<Registration> result = new ArrayList<>();
        for (Registration reg : registrations.values()) {
            if (reg.getStudentId().equals(studentId)) {
                result.add(reg);
            }
        }
        return result;
    }

    private void notifyRegistrationSuccess(String studentId, String examId, String slotId, String roomId) {
        for (AlertObserver observer : observers) {
            observer.onRegistrationSuccess(studentId, examId, slotId, roomId);
        }
    }

    private void notifyRescheduleSuccess(String studentId, String examId, String oldSlotId, String newSlotId, String newRoomId) {
        for (AlertObserver observer : observers) {
            observer.onRescheduleSuccess(studentId, examId, oldSlotId, newSlotId, newRoomId);
        }
    }

    private void notifyConflictAlert(String studentId, String requestedSlotId, String reason) {
        for (AlertObserver observer : observers) {
            observer.onSchedulingConflictAlert(studentId, requestedSlotId, reason);
        }
    }

    private void notifyCapacityAlert(String slotId, String roomId, int filled, int maxCapacity) {
        for (AlertObserver observer : observers) {
            observer.onCapacityAlert(slotId, roomId, filled, maxCapacity);
        }
    }

    private void notifyCheckInSuccess(String ticketId, String studentId, String slotId, String roomId) {
        for (AlertObserver observer : observers) {
            observer.onCheckInSuccess(ticketId, studentId, slotId, roomId);
        }
    }

    private void notifyNoShow(String studentId, String examId, String slotId) {
        for (AlertObserver observer : observers) {
            observer.onNoShowRecorded(studentId, examId, slotId);
        }
    }
}
