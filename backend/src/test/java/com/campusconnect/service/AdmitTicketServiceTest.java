package com.campusconnect.service;

import com.campusconnect.common.exception.AdmitTicketAlreadyUsedException;
import com.campusconnect.common.exception.InvalidTicketException;
import com.campusconnect.model.Exam;
import com.campusconnect.model.ExamSlot;
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

class AdmitTicketServiceTest {

    private RegistrationRepository registrationRepository;
    private AdmitTicketRepository admitTicketRepository;

    private StudentService studentService;
    private ExamService examService;
    private ExamSlotService examSlotService;
    private ProctoringRoomService proctoringRoomService;
    private RegistrationService registrationService;
    private AdmitTicketService admitTicketService;

    @BeforeEach
    void setUp() {
        StudentRepository studentRepository = new StudentRepository();
        ExamRepository examRepository = new ExamRepository();
        ExamSlotRepository examSlotRepository = new ExamSlotRepository();
        ProctoringRoomRepository proctoringRoomRepository = new ProctoringRoomRepository();
        registrationRepository = new RegistrationRepository();
        admitTicketRepository = new AdmitTicketRepository();

        studentService = new StudentService(studentRepository);
        examService = new ExamService(examRepository);
        examSlotService = new ExamSlotService(examSlotRepository, examRepository);
        proctoringRoomService = new ProctoringRoomService(proctoringRoomRepository, examSlotRepository);

        registrationService = new RegistrationService(
                registrationRepository, examSlotRepository, examRepository, studentRepository,
                proctoringRoomRepository, admitTicketRepository, new FillFirstRoomAllocationStrategy());
        admitTicketService = new AdmitTicketService(admitTicketRepository, registrationRepository);
    }

    @Test
    void checkIn_rejectsUnknownTicketAsInvalid() {
        assertThrows(InvalidTicketException.class, () -> admitTicketService.checkIn("no-such-ticket-id"));
    }

    @Test
    void checkIn_rejectsTicketThatWasAlreadyUsed() {
        Student student = studentService.createStudent("Checked-in Student");
        Exam exam = examService.createExam("Practical", "desc");
        ExamSlot slot = examSlotService.createSlot(exam.getId(), LocalDateTime.now().plusDays(1).toLocalDate(),
                LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).toLocalTime(), 60);
        proctoringRoomService.createRoom(slot.getId(), 2);

        RegistrationResult result = registrationService.register(student.getId(), slot.getId());
        String ticketId = result.getAdmitTicket().getId();

        admitTicketService.checkIn(ticketId); 
        assertEquals(Status.CHECKED_IN, registrationRepository.findById(result.getRegistration().getId()).orElseThrow().getStatus());

        assertThrows(AdmitTicketAlreadyUsedException.class, () -> admitTicketService.checkIn(ticketId));
    }

    @Test
    void checkIn_rejectsTicketSupersededByReschedule() {
        Student student = studentService.createStudent("Rescheduling Student");
        Exam exam = examService.createExam("Practical", "desc");
        ExamSlot originalSlot = examSlotService.createSlot(exam.getId(), LocalDateTime.now().plusDays(2).toLocalDate(),
                LocalDateTime.now().plusDays(2).withHour(9).withMinute(0).toLocalTime(), 60);
        proctoringRoomService.createRoom(originalSlot.getId(), 2);
        ExamSlot newSlot = examSlotService.createSlot(exam.getId(), LocalDateTime.now().plusDays(3).toLocalDate(),
                LocalDateTime.now().plusDays(3).withHour(9).withMinute(0).toLocalTime(), 60);
        proctoringRoomService.createRoom(newSlot.getId(), 2);

        RegistrationResult original = registrationService.register(student.getId(), originalSlot.getId());
        String originalTicketId = original.getAdmitTicket().getId();

        registrationService.reschedule(original.getRegistration().getId(), newSlot.getId());

        assertThrows(InvalidTicketException.class, () -> admitTicketService.checkIn(originalTicketId));
    }
}
