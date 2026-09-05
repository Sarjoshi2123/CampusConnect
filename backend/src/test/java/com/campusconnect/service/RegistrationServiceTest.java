package com.campusconnect.service;

import com.campusconnect.common.exception.DifferentExamRescheduleException;
import com.campusconnect.common.exception.RoomCapacityExceededException;
import com.campusconnect.common.exception.SlotConflictException;
import com.campusconnect.common.exception.InvalidRescheduleTimeException;
import com.campusconnect.model.Exam;
import com.campusconnect.model.ExamSlot;
import com.campusconnect.model.ProctoringRoom;
import com.campusconnect.model.Registration;
import com.campusconnect.model.Registration.Status;
import com.campusconnect.model.Student;
import com.campusconnect.repository.AdmitTicketRepository;
import com.campusconnect.repository.ExamRepository;
import com.campusconnect.repository.ExamSlotRepository;
import com.campusconnect.repository.ProctoringRoomRepository;
import com.campusconnect.repository.RegistrationRepository;
import com.campusconnect.repository.StudentRepository;
import com.campusconnect.service.strategy.FillFirstRoomAllocationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Purpose: Unit tests for RegistrationService, focused on the business-specific
 *          edge cases called out in the CampusConnect spec: cross-exam
 *          self-conflict, room capacity exhaustion, and a reschedule that either
 *          creates a new conflict or is attempted too late.
 * Role: Test suite for the service package. Uses real (in-memory) repository and
 *       service instances rather than mocks — the repositories are plain
 *       ConcurrentHashMap-backed classes with no external dependencies, so
 *       exercising the real conflict/allocation logic end-to-end is both simpler
 *       and more faithful than mocking each collaborator individually.
 */
class RegistrationServiceTest {

    private StudentRepository studentRepository;
    private ExamRepository examRepository;
    private ExamSlotRepository examSlotRepository;
    private ProctoringRoomRepository proctoringRoomRepository;
    private RegistrationRepository registrationRepository;
    private AdmitTicketRepository admitTicketRepository;

    private StudentService studentService;
    private ExamService examService;
    private ExamSlotService examSlotService;
    private ProctoringRoomService proctoringRoomService;
    private RegistrationService registrationService;

    @BeforeEach
    void setUp() {
        studentRepository = new StudentRepository();
        examRepository = new ExamRepository();
        examSlotRepository = new ExamSlotRepository();
        proctoringRoomRepository = new ProctoringRoomRepository();
        registrationRepository = new RegistrationRepository();
        admitTicketRepository = new AdmitTicketRepository();

        studentService = new StudentService(studentRepository);
        examService = new ExamService(examRepository);
        examSlotService = new ExamSlotService(examSlotRepository, examRepository);
        proctoringRoomService = new ProctoringRoomService(proctoringRoomRepository, examSlotRepository);

        registrationService = new RegistrationService(
                registrationRepository, examSlotRepository, examRepository, studentRepository,
                proctoringRoomRepository, admitTicketRepository, new FillFirstRoomAllocationStrategy());
    }

    /**
     * Edge case: a student registered for one exam slot must be rejected when
     * trying to register for a *different exam's* slot (e.g. run by a different
     * partner university) whose time overlaps — the conflict check must span
     * every exam the student is in, not just the same exam.
     */
    @Test
    void register_rejectsSelfConflictAcrossDifferentExams() {
        Student student = studentService.createStudent("Asha Rao");

        Exam examA = examService.createExam("Partner University A - Midterm", "desc A");
        ExamSlot slotA = examSlotService.createSlot(examA.getId(), LocalDateTime.now().plusDays(1).toLocalDate(),
                LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).toLocalTime(), 60);
        proctoringRoomService.createRoom(slotA.getId(), 5);

        Exam examB = examService.createExam("Partner University B - Midterm", "desc B");
        // Overlaps slotA (10:00-11:00): starts 10:30, same day.
        ExamSlot slotB = examSlotService.createSlot(examB.getId(), slotA.getDate(),
                slotA.getStartTime().plusMinutes(30), 60);
        proctoringRoomService.createRoom(slotB.getId(), 5);

        registrationService.register(student.getId(), slotA.getId());

        SlotConflictException ex = assertThrows(SlotConflictException.class,
                () -> registrationService.register(student.getId(), slotB.getId()));
        assertEquals(1, registrationRepository.findAllRegistrationsByStudentId(student.getId()).size());
        org.junit.jupiter.api.Assertions.assertNotNull(ex.getMessage());
    }

    /**
     * Edge case: every room belonging to the slot is already at capacity when a
     * new registration attempt comes in.
     */
    @Test
    void register_throwsWhenAllRoomsAreFull() {
        Student studentX = studentService.createStudent("Student X");
        Student studentY = studentService.createStudent("Student Y");

        Exam exam = examService.createExam("Finals", "desc");
        ExamSlot slot = examSlotService.createSlot(exam.getId(), LocalDateTime.now().plusDays(1).toLocalDate(),
                LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).toLocalTime(), 90);
        proctoringRoomService.createRoom(slot.getId(), 1); // only one seat, in one room

        registrationService.register(studentX.getId(), slot.getId());

        assertThrows(RoomCapacityExceededException.class,
                () -> registrationService.register(studentY.getId(), slot.getId()));
        // studentY must not have been registered as a side effect of the failed attempt.
        assertEquals(0, registrationRepository.findAllRegistrationsByStudentId(studentY.getId()).size());
    }

    /**
     * Edge case: rescheduling into a slot that conflicts with *another* active
     * registration the student already holds must fail, and must leave the
     * original registration/room seat completely untouched.
     */
    @Test
    void reschedule_rejectsWhenNewSlotConflictsWithAnotherActiveRegistration() {
        Student student = studentService.createStudent("Multi Exam Student");

        Exam examA = examService.createExam("Exam A", "desc");
        ExamSlot originalSlot = examSlotService.createSlot(examA.getId(), LocalDateTime.now().plusDays(2).toLocalDate(),
                LocalDateTime.now().plusDays(2).withHour(9).withMinute(0).toLocalTime(), 60);
        ProctoringRoom originalRoom = proctoringRoomService.createRoom(originalSlot.getId(), 3);

        Exam examB = examService.createExam("Exam B", "desc");
        ExamSlot otherActiveSlot = examSlotService.createSlot(examB.getId(), originalSlot.getDate(),
                originalSlot.getStartTime().plusHours(4), 60); // 13:00-14:00, no overlap with originalSlot
        proctoringRoomService.createRoom(otherActiveSlot.getId(), 3);

        // Same exam as the original registration (examA), so the reschedule passes
        // the same-exam check and reaches the conflict check against otherActiveSlot.
        ExamSlot conflictingTargetSlot = examSlotService.createSlot(examA.getId(), originalSlot.getDate(),
                otherActiveSlot.getStartTime().plusMinutes(30), 60); // 13:30-14:30, overlaps otherActiveSlot
        proctoringRoomService.createRoom(conflictingTargetSlot.getId(), 3);

        RegistrationResult originalResult = registrationService.register(student.getId(), originalSlot.getId());
        registrationService.register(student.getId(), otherActiveSlot.getId());

        assertThrows(SlotConflictException.class, () -> registrationService.reschedule(
                originalResult.getRegistration().getId(), conflictingTargetSlot.getId()));

        Registration reloaded = registrationRepository.findById(originalResult.getRegistration().getId()).orElseThrow();
        assertEquals(Status.REGISTERED, reloaded.getStatus());
        ProctoringRoom reloadedRoom = proctoringRoomRepository.findById(originalRoom.getId()).orElseThrow();
        assertEquals(1, reloadedRoom.getCurrentOccupancy());
    }

    /**
     * Edge case: a reschedule submitted once the check-in window for the
     * student's current slot has already opened (now >= slot start) must be
     * rejected outright, before any conflict/capacity check on the new slot.
     */
    @Test
    void reschedule_rejectsOnceCheckInWindowHasOpened() {
        Student student = studentService.createStudent("Late Rescheduler");

        Exam exam = examService.createExam("Ongoing Exam", "desc");
        LocalDateTime pastStart = LocalDateTime.now().minusHours(1);
        ExamSlot currentSlot = examSlotService.createSlot(
                exam.getId(), pastStart.toLocalDate(), pastStart.toLocalTime(), 120); // window opened 1h ago, closes in 1h
        proctoringRoomService.createRoom(currentSlot.getId(), 2);

        ExamSlot futureSlot = examSlotService.createSlot(exam.getId(), LocalDateTime.now().plusDays(3).toLocalDate(),
                LocalDateTime.now().plusDays(3).withHour(9).withMinute(0).toLocalTime(), 60);
        proctoringRoomService.createRoom(futureSlot.getId(), 2);

        // Register directly against the repository to seed a REGISTERED registration
        // for an already-started slot (RegistrationService#register does not itself
        // forbid registering into a slot whose window already opened; only reschedule
        // enforces this cutoff).
        RegistrationResult result = registrationService.register(student.getId(), currentSlot.getId());

        assertThrows(InvalidRescheduleTimeException.class, () -> registrationService.reschedule(
                result.getRegistration().getId(), futureSlot.getId()));
    }

    /**
     * Edge case: a reschedule must stay within the same exam — moving a student
     * onto a slot belonging to a different exam is rejected outright, even when
     * that slot has no conflict and plenty of capacity, and must leave the
     * original registration/room seat completely untouched.
     */
    @Test
    void reschedule_rejectsWhenNewSlotBelongsToDifferentExam() {
        Student student = studentService.createStudent("Cross Exam Rescheduler");

        Exam examA = examService.createExam("Exam A", "desc");
        ExamSlot originalSlot = examSlotService.createSlot(examA.getId(), LocalDateTime.now().plusDays(2).toLocalDate(),
                LocalDateTime.now().plusDays(2).withHour(9).withMinute(0).toLocalTime(), 60);
        ProctoringRoom originalRoom = proctoringRoomService.createRoom(originalSlot.getId(), 3);

        Exam examB = examService.createExam("Exam B", "desc");
        ExamSlot differentExamSlot = examSlotService.createSlot(examB.getId(), LocalDateTime.now().plusDays(5).toLocalDate(),
                LocalDateTime.now().plusDays(5).withHour(9).withMinute(0).toLocalTime(), 60); // no overlap, plenty of capacity
        proctoringRoomService.createRoom(differentExamSlot.getId(), 3);

        RegistrationResult originalResult = registrationService.register(student.getId(), originalSlot.getId());

        assertThrows(DifferentExamRescheduleException.class, () -> registrationService.reschedule(
                originalResult.getRegistration().getId(), differentExamSlot.getId()));

        Registration reloaded = registrationRepository.findById(originalResult.getRegistration().getId()).orElseThrow();
        assertEquals(Status.REGISTERED, reloaded.getStatus());
        ProctoringRoom reloadedRoom = proctoringRoomRepository.findById(originalRoom.getId()).orElseThrow();
        assertEquals(1, reloadedRoom.getCurrentOccupancy());
    }
}
