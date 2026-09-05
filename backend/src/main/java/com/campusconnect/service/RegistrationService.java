package com.campusconnect.service;

import com.campusconnect.common.exception.DifferentExamRescheduleException;
import com.campusconnect.common.exception.ExamNotFoundException;
import com.campusconnect.common.exception.ExamSlotNotFoundException;
import com.campusconnect.common.exception.InvalidRescheduleTimeException;
import com.campusconnect.common.exception.ProctoringRoomNotFoundException;
import com.campusconnect.common.exception.RegistrationNotFoundException;
import com.campusconnect.common.exception.RoomCapacityExceededException;
import com.campusconnect.common.exception.SlotConflictException;
import com.campusconnect.common.exception.StudentNotFoundException;
import com.campusconnect.model.AdmitTicket;
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
import com.campusconnect.service.strategy.RoomAllocationStrategy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final ExamSlotRepository examSlotRepository;
    private final ExamRepository examRepository;
    private final StudentRepository studentRepository;
    private final ProctoringRoomRepository proctoringRoomRepository;
    private final AdmitTicketRepository admitTicketRepository;
    private final RoomAllocationStrategy roomAllocationStrategy;

    private final Object lock = new Object();

    public RegistrationService(RegistrationRepository registrationRepository,
                                ExamSlotRepository examSlotRepository,
                                ExamRepository examRepository,
                                StudentRepository studentRepository,
                                ProctoringRoomRepository proctoringRoomRepository,
                                AdmitTicketRepository admitTicketRepository,
                                RoomAllocationStrategy roomAllocationStrategy) {
        this.registrationRepository = registrationRepository;
        this.examSlotRepository = examSlotRepository;
        this.examRepository = examRepository;
        this.studentRepository = studentRepository;
        this.proctoringRoomRepository = proctoringRoomRepository;
        this.admitTicketRepository = admitTicketRepository;
        this.roomAllocationStrategy = roomAllocationStrategy;
    }

    public RegistrationResult register(String studentId, String examSlotId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found: " + studentId));
        ExamSlot targetSlot = examSlotRepository.findById(examSlotId)
                .orElseThrow(() -> new ExamSlotNotFoundException("Exam slot not found: " + examSlotId));
        Exam exam = examRepository.findById(targetSlot.getExamId())
                .orElseThrow(() -> new ExamNotFoundException("Exam not found: " + targetSlot.getExamId()));

        synchronized (lock) {
            assertNoConflict(student.getId(), targetSlot, null);
            ProctoringRoom room = allocateRoom(targetSlot.getId());

            Registration registration = new Registration(
                    UUID.randomUUID().toString(), student.getId(), exam.getId(), targetSlot.getId(),
                    room.getId(), LocalDateTime.now(), Status.REGISTERED);
            registrationRepository.save(registration);

            AdmitTicket ticket = issueTicket(registration);
            return new RegistrationResult(registration, ticket);
        }
    }

    public RegistrationResult reschedule(String registrationId, String newExamSlotId) {
        Registration oldRegistration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RegistrationNotFoundException("Registration not found: " + registrationId));

        if (oldRegistration.getStatus() != Status.REGISTERED) {
            throw new InvalidRescheduleTimeException(
                    "Registration " + registrationId + " is not eligible for reschedule (status: "
                            + oldRegistration.getStatus() + ")");
        }

        ExamSlot oldSlot = examSlotRepository.findById(oldRegistration.getExamSlotId())
                .orElseThrow(() -> new ExamSlotNotFoundException("Exam slot not found: " + oldRegistration.getExamSlotId()));

        LocalDateTime oldSlotStart = LocalDateTime.of(oldSlot.getDate(), oldSlot.getStartTime());
        if (!LocalDateTime.now().isBefore(oldSlotStart)) {
            throw new InvalidRescheduleTimeException(
                    "Cannot reschedule registration " + registrationId
                            + ": the check-in window for its current slot has already opened");
        }

        ExamSlot newSlot = examSlotRepository.findById(newExamSlotId)
                .orElseThrow(() -> new ExamSlotNotFoundException("Exam slot not found: " + newExamSlotId));
        Exam newExam = examRepository.findById(newSlot.getExamId())
                .orElseThrow(() -> new ExamNotFoundException("Exam not found: " + newSlot.getExamId()));

        if (!newExam.getId().equals(oldRegistration.getExamId())) {
            throw new DifferentExamRescheduleException(
                    "Cannot reschedule registration " + registrationId + " to exam slot " + newExamSlotId
                            + ": that slot belongs to exam " + newExam.getId()
                            + ", but the registration is for exam " + oldRegistration.getExamId()
                            + " — reschedule may only move between slots of the same exam");
        }

        synchronized (lock) {
            assertNoConflict(oldRegistration.getStudentId(), newSlot, oldRegistration.getId());

            ProctoringRoom newRoom = allocateRoom(newSlot.getId());

            ProctoringRoom oldRoom = proctoringRoomRepository.findById(oldRegistration.getProctoringRoomId())
                    .orElseThrow(() -> new ProctoringRoomNotFoundException(
                            "Proctoring room not found: " + oldRegistration.getProctoringRoomId()));
            oldRoom.decrementOccupancy();
            proctoringRoomRepository.save(oldRoom);

            oldRegistration.setStatus(Status.CANCELLED);
            registrationRepository.save(oldRegistration);
            
            Registration newRegistration = new Registration(
                    UUID.randomUUID().toString(), oldRegistration.getStudentId(), newExam.getId(),
                    newSlot.getId(), newRoom.getId(), LocalDateTime.now(), Status.REGISTERED);
            registrationRepository.save(newRegistration);

            AdmitTicket newTicket = issueTicket(newRegistration);
            return new RegistrationResult(newRegistration, newTicket);
        }
    }

    public Registration getRegistration(String registrationId) {
        return registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RegistrationNotFoundException("Registration not found: " + registrationId));
    }

    private void assertNoConflict(String studentId, ExamSlot candidateSlot, String excludeRegistrationId) {
        List<Registration> others = registrationRepository.findAllRegistrationsByStudentId(studentId).stream()
                .filter(r -> r.getStatus() == Status.REGISTERED || r.getStatus() == Status.CHECKED_IN)
                .filter(r -> excludeRegistrationId == null || !r.getId().equals(excludeRegistrationId))
                .collect(Collectors.toList());

        for (Registration other : others) {
            ExamSlot otherSlot = examSlotRepository.findById(other.getExamSlotId())
                    .orElseThrow(() -> new ExamSlotNotFoundException("Exam slot not found: " + other.getExamSlotId()));
            if (overlaps(candidateSlot, otherSlot)) {
                throw new SlotConflictException(
                        "Student " + studentId + " already has an active registration (exam slot "
                                + otherSlot.getId() + ") that overlaps with the requested slot " + candidateSlot.getId());
            }
        }
    }

    private boolean overlaps(ExamSlot a, ExamSlot b) {
        if (!a.getDate().equals(b.getDate())) {
            return false;
        }
        return a.getStartTime().isBefore(b.getEndTime()) && b.getStartTime().isBefore(a.getEndTime());
    }

    private ProctoringRoom allocateRoom(String examSlotId) {
        List<ProctoringRoom> rooms = proctoringRoomRepository.findByExamSlotId(examSlotId);
        ProctoringRoom room = roomAllocationStrategy.selectRoom(rooms)
                .orElseThrow(() -> new RoomCapacityExceededException(
                        "No proctoring room with available capacity for exam slot " + examSlotId));
        room.incrementOccupancy();
        return proctoringRoomRepository.save(room);
    }

    private AdmitTicket issueTicket(Registration registration) {
        AdmitTicket ticket = new AdmitTicket(
                UUID.randomUUID().toString(), registration.getStudentId(), registration.getExamId(),
                registration.getExamSlotId(), registration.getProctoringRoomId(), registration.getId());
        return admitTicketRepository.save(ticket);
    }
}
