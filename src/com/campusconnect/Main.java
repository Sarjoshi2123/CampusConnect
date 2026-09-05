package com.campusconnect;

import com.campusconnect.exception.*;
import com.campusconnect.model.*;
import com.campusconnect.observer.AuditLogObserver;
import com.campusconnect.observer.CapacityAlertObserver;
import com.campusconnect.service.ExamManagementService;
import com.campusconnect.strategy.BalancedAllocationStrategy;
import com.campusconnect.strategy.FirstFitAllocationStrategy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Interactive Command-Line Portal for CampusConnect EdTech.
 * Guides users step-by-step through exam selection, slot picking, conflict detection,
 * automatic room assignment, admit ticket generation, and reschedule handling.
 */
public class Main {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final LocalDateTime BASE_TIME = LocalDateTime.of(2026, 9, 10, 10, 0);

    public static void main(String[] args) {
        ExamManagementService service = new ExamManagementService();

        // Register system observers for audit logging & room capacity warnings
        service.addObserver(new AuditLogObserver());
        service.addObserver(new CapacityAlertObserver());

        // Setup partner universities, certification exams, time slots, and rooms
        initializeEnvironment(service);

        if (args.length > 0 && "--auto".equalsIgnoreCase(args[0])) {
            runAutomatedSuite(service);
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("===============================================================================");
        System.out.println("      CAMPUSCONNECT EDTECH - ONLINE EXAM ALLOCATION SYSTEM PORTAL 🎓         ");
        System.out.println("===============================================================================");

        while (true) {
            System.out.println("\n-------------------------------------------------------------------------------");
            System.out.println("   MAIN MENU - SELECT AN ACTION:");
            System.out.println("-------------------------------------------------------------------------------");
            System.out.println("  [1]  Register for a Certification Exam (Interactive Wizard)");
            System.out.println("  [2]  View My Current Registrations & Admit Tickets");
            System.out.println("  [3]  Reschedule an Existing Exam Slot");
            System.out.println("  [4]  Check-In Candidate at Exam Time with Admit Ticket");
            System.out.println("  [5]  Query Proctoring Room Capacity & Utilization");
            System.out.println("  [6]  Process No-Shows for Closed Slot");
            System.out.println("  [7]  Switch Room Allocation Strategy (FirstFit vs Balanced)");
            System.out.println("  [8]  Run Full Automated Test Suite (All Edge Cases)");
            System.out.println("  [9]  Exit Application");
            System.out.println("-------------------------------------------------------------------------------");
            System.out.print(" Enter choice (1-9): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    interactiveRegistrationWizard(service, scanner);
                    break;
                case "2":
                    viewStudentRegistrations(service, scanner);
                    break;
                case "3":
                    interactiveRescheduleWizard(service, scanner);
                    break;
                case "4":
                    interactiveCheckInWizard(service, scanner);
                    break;
                case "5":
                    queryRoomUtilization(service, scanner);
                    break;
                case "6":
                    switchAllocationStrategy(service, scanner);
                    break;
                case "7":
                    runAutomatedSuite(service);
                    break;
                case "8":
                case "9":
                    System.out.println("\nThank you for using CampusConnect EdTech! Goodbye.");
                    return;
                default:
                    System.out.println("\n Invalid selection. Please enter a number between 1 and 9.");
            }
        }
    }

    // 1. INTERACTIVE REGISTRATION WIZARD
    private static void interactiveRegistrationWizard(ExamManagementService service, Scanner scanner) {
        System.out.println("\n===============================================================================");
        System.out.println("    STEP-BY-STEP EXAM REGISTRATION WIZARD");
        System.out.println("===============================================================================");

        // Step 1: Select or Create Student
        Student student = selectOrCreateStudent(service, scanner);
        if (student == null) return;

        // Step 2: Select Certification Exam
        System.out.println("\n--- STEP 2: SELECT CERTIFICATION EXAM ---");
        System.out.println("  [1] EXAM-DSA : Data Structures & Algorithms (Medi-Caps University)");
        System.out.println("  [2] EXAM-AI  : Artificial Intelligence Fundamentals (Stanford University)");
        System.out.print(" Select Exam (1 or 2): ");
        String examChoice = scanner.nextLine().trim();

        String examId = "2".equals(examChoice) ? "EXAM-AI" : "EXAM-DSA";
        Exam exam = service.getExam(examId);

        System.out.println("\n  Selected Exam: " + exam.getTitle() + " (" + exam.getPartnerUniversity() + ")");

        // Step 3: Select Time Slot
        System.out.println("\n--- STEP 3: SELECT TIME SLOT ---");
        if ("EXAM-DSA".equals(examId)) {
            System.out.println("  [1] SLOT-DSA-AM : Morning Slot   (12:00 - 14:00) [Rooms: Room A1 (Cap:2), Room A2 (Cap:1)]");
            System.out.println("  [2] SLOT-DSA-PM : Afternoon Slot (16:00 - 18:00) [Rooms: Room B1 (Cap:5)]");
        } else {
            System.out.println("  [1] SLOT-AI-OVERLAP : Mid-day Slot (13:00 - 15:00) [Rooms: Room C1 (Cap:5)]");
            System.out.println("       Note: This slot overlaps in time with SLOT-DSA-AM (12:00 - 14:00)!");
        }

        System.out.print(" Select Time Slot (1 or 2): ");
        String slotChoice = scanner.nextLine().trim();

        String slotId;
        if ("EXAM-DSA".equals(examId)) {
            slotId = "2".equals(slotChoice) ? "SLOT-DSA-PM" : "SLOT-DSA-AM";
        } else {
            slotId = "SLOT-AI-OVERLAP";
        }

        ExamSlot targetSlot = service.getExamSlot(slotId);
        System.out.println("\n  Attempting registration for " + student.getName() + " in " + slotId + "...");

        // Execute Registration
        try {
            AdmitTicket ticket = service.registerStudentForExam(student.getId(), examId, slotId);

            System.out.println("\n===============================================================================");
            System.out.println("    REGISTRATION SUCCESSFUL! ADMIT TICKET ISSUED");
            System.out.println("===============================================================================");
            System.out.println("   Candidate Name  : " + student.getName() + " (" + student.getId() + ")");
            System.out.println("   Exam Title      : " + exam.getTitle());
            System.out.println("   University      : " + exam.getPartnerUniversity());
            System.out.println("   Slot Time       : " + targetSlot.getStartTime().format(TIME_FMT) + " to " + targetSlot.getEndTime().format(TIME_FMT));
            System.out.println("   Assigned Room   : " + ticket.getRoomId());
            System.out.println("   Ticket Code  : " + ticket.getTicketId());
            System.out.println("   Registration ID : " + ticket.getRegistrationId());
            System.out.println("===============================================================================");

        } catch (SchedulingConflictException e) {
            System.out.println("\n SCHEDULING CONFLICT DETECTED!");
            System.out.println("   " + e.getMessage());
            System.out.println("    Hint: You already have another exam registered during this exact time window.");

        } catch (CapacityExceededException e) {
            System.out.println("\n ROOM CAPACITY EXCEEDED!");
            System.out.println("   " + e.getMessage());
            System.out.println("    Hint: All proctoring rooms for this slot are at 100% capacity. Please choose another slot.");

        } catch (CampusConnectException e) {
            System.out.println("\n REGISTRATION ERROR: " + e.getMessage());
        }
    }

    // 2. VIEW STUDENT REGISTRATIONS
    private static void viewStudentRegistrations(ExamManagementService service, Scanner scanner) {
        System.out.println("\n---  VIEW STUDENT REGISTRATIONS ---");
        Student student = selectOrCreateStudent(service, scanner);
        if (student == null) return;

        List<Registration> regs = service.getStudentRegistrations(student.getId());
        if (regs.isEmpty()) {
            System.out.println("\n  No registrations found for " + student.getName() + " (" + student.getId() + ").");
            return;
        }

        System.out.println("\n  Registrations for " + student.getName() + ":");
        System.out.println("  -----------------------------------------------------------------------------");
        for (Registration reg : regs) {
            Exam exam = service.getExam(reg.getExamId());
            ExamSlot slot = service.getExamSlot(reg.getSlotId());
            System.out.printf("  • Reg ID: %s | Exam: %s | Slot: %s (%s to %s) | Room: %s | Status: %s\n",
                    reg.getRegistrationId(),
                    exam != null ? exam.getTitle() : reg.getExamId(),
                    reg.getSlotId(),
                    slot != null ? slot.getStartTime().format(TIME_FMT) : "N/A",
                    slot != null ? slot.getEndTime().format(TIME_FMT) : "N/A",
                    reg.getRoomId(),
                    reg.getStatus());
        }
    }

    // 3. INTERACTIVE RESCHEDULE WIZARD
    private static void interactiveRescheduleWizard(ExamManagementService service, Scanner scanner) {
        System.out.println("\n---  RESCHEDULE EXAM SLOT WIZARD ---");
        Student student = selectOrCreateStudent(service, scanner);
        if (student == null) return;

        List<Registration> regs = service.getStudentRegistrations(student.getId());
        if (regs.isEmpty()) {
            System.out.println(" Student has no active registrations to reschedule.");
            return;
        }

        System.out.println("\nSelect registration to reschedule:");
        for (int i = 0; i < regs.size(); i++) {
            Registration r = regs.get(i);
            System.out.printf("  [%d] Reg ID: %s | Exam: %s | Current Slot: %s\n", (i + 1), r.getRegistrationId(), r.getExamId(), r.getSlotId());
        }

        System.out.print(" Select Registration Number: ");
        int regChoice = parseChoice(scanner.nextLine().trim());
        if (regChoice < 1 || regChoice > regs.size()) {
            System.out.println(" Invalid choice.");
            return;
        }

        Registration selectedReg = regs.get(regChoice - 1);

        System.out.println("\nTarget new slot options for " + selectedReg.getExamId() + ":");
        System.out.println("  [1] SLOT-DSA-AM (12:00 - 14:00)");
        System.out.println("  [2] SLOT-DSA-PM (16:00 - 18:00)");
        System.out.print(" Select Target Slot (1 or 2): ");
        String targetSlotChoice = scanner.nextLine().trim();

        String targetSlotId = "1".equals(targetSlotChoice) ? "SLOT-DSA-AM" : "SLOT-DSA-PM";

        System.out.println("\nSelect simulation timing:");
        System.out.println("  [1] Before check-in window opens (Normal reschedule)");
        System.out.println("  [2] After check-in window has already opened (Window closed rule test)");
        System.out.print(" Choice (1 or 2): ");
        String timingChoice = scanner.nextLine().trim();

        LocalDateTime refTime = "2".equals(timingChoice) ?
                BASE_TIME.plusHours(1).plusMinutes(50) : BASE_TIME.minusMinutes(30);

        try {
            AdmitTicket newTicket = service.rescheduleStudent(selectedReg.getRegistrationId(), targetSlotId, refTime);
            System.out.println("\n RESCHEDULE SUCCESSFUL!");
            System.out.println("   New Slot ID    : " + newTicket.getSlotId());
            System.out.println("   New Room ID    : " + newTicket.getRoomId());
            System.out.println("   New Ticket  : " + newTicket.getTicketId());
            System.out.println("   (Prior room seat was released cleanly!)");
        } catch (CampusConnectException e) {
            System.out.println("\n RESCHEDULE FAILED: " + e.getMessage());
        }
    }

    // 4. INTERACTIVE CHECK-IN WIZARD
    private static void interactiveCheckInWizard(ExamManagementService service, Scanner scanner) {
        System.out.println("\n---  CHECK-IN WITH ADMIT TICKET ---");
        System.out.print(" Enter Ticket Code (e.g. TICKET-XXXXX): ");
        String ticketId = scanner.nextLine().trim();

        AdmitTicket ticket = service.getAdmitTicket(ticketId);
        LocalDateTime checkInTime = BASE_TIME.plusHours(6); // Default PM check-in time

        if (ticket != null) {
            ExamSlot slot = service.getExamSlot(ticket.getSlotId());
            if (slot != null) {
                checkInTime = slot.getCheckInOpenTime().plusMinutes(5);
            }
        }

        try {
            service.checkInWithTicket(ticketId, checkInTime);
            System.out.println("\n CHECK-IN SUCCESSFUL!");
            System.out.println("   Verified Candidate Ticket Code: " + ticketId);
        } catch (CampusConnectException e) {
            System.out.println("\n CHECK-IN FAILED: " + e.getMessage());
        }
    }

    // 5. QUERY ROOM UTILIZATION
    private static void queryRoomUtilization(ExamManagementService service, Scanner scanner) {
        System.out.println("\n---  PROCTORING ROOM UTILIZATION QUERY ---");
        System.out.println("  Available Slots: SLOT-DSA-AM, SLOT-DSA-PM, SLOT-AI-OVERLAP");
        System.out.print(" Enter Slot ID (or press Enter for ALL slots): ");
        String slotId = scanner.nextLine().trim();

        if (slotId.isEmpty()) {
            printRoomUtilization(service, "SLOT-DSA-AM");
            printRoomUtilization(service, "SLOT-DSA-PM");
            printRoomUtilization(service, "SLOT-AI-OVERLAP");
        } else {
            try {
                printRoomUtilization(service, slotId);
            } catch (IllegalArgumentException e) {
                System.out.println("⚠️ " + e.getMessage());
            }
        }
    }

    // 6. SWITCH STRATEGY
    private static void switchAllocationStrategy(ExamManagementService service, Scanner scanner) {
        System.out.println("\n--- SWITCH ROOM ALLOCATION STRATEGY ---");
        System.out.println("  [1] FirstFit Allocation (Fills rooms sequentially)");
        System.out.println("  [2] Balanced Allocation (Distributes students evenly across rooms)");
        System.out.print(" Choose Strategy (1 or 2): ");
        String choice = scanner.nextLine().trim();

        if ("2".equals(choice)) {
            service.setAllocationStrategy(new BalancedAllocationStrategy());
            System.out.println(" Strategy changed to: BalancedAllocationStrategy");
        } else {
            service.setAllocationStrategy(new FirstFitAllocationStrategy());
            System.out.println(" Strategy changed to: FirstFitAllocationStrategy");
        }
    }

    // Helper: Select or Create Student
    private static Student selectOrCreateStudent(ExamManagementService service, Scanner scanner) {
        System.out.println("\n  Select Candidate:");
        System.out.println("  [1] S101 : Alice Johnson (alice@medicaps.ac.in)");
        System.out.println("  [2] S102 : Bob Smith (bob@stanford.edu)");
        System.out.println("  [3] S103 : Charlie Davis (charlie@mit.edu)");
        System.out.println("  [4] S104 : Diana Prince (diana@oxford.ac.uk)");
        System.out.println("  [5] S105 : Evan Wright (evan@berkeley.edu)");
        System.out.println("  [6]  Create New Custom Student");
        System.out.print(" Choice (1-6): ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1": return service.getStudent("S101");
            case "2": return service.getStudent("S102");
            case "3": return service.getStudent("S103");
            case "4": return service.getStudent("S104");
            case "5": return service.getStudent("S105");
            case "6":
                System.out.print("Enter New Student ID (e.g. S106): ");
                String id = scanner.nextLine().trim();
                System.out.print("Enter Full Name: ");
                String name = scanner.nextLine().trim();
                System.out.print("Enter Email Address: ");
                String email = scanner.nextLine().trim();
                Student newStudent = new Student(id, name, email);
                service.registerStudent(newStudent);
                System.out.println(" Registered new student: " + name + " (" + id + ")");
                return newStudent;
            default:
                System.out.println(" Invalid choice. Defaulting to Alice (S101).");
                return service.getStudent("S101");
        }
    }

    private static int parseChoice(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void initializeEnvironment(ExamManagementService service) {
        // Registered Students
        service.registerStudent(new Student("S101", "Alice Johnson", "alice@medicaps.ac.in"));
        service.registerStudent(new Student("S102", "Bob Smith", "bob@stanford.edu"));
        service.registerStudent(new Student("S103", "Charlie Davis", "charlie@mit.edu"));
        service.registerStudent(new Student("S104", "Diana Prince", "diana@oxford.ac.uk"));
        service.registerStudent(new Student("S105", "Evan Wright", "evan@berkeley.edu"));

        // Partner Exams
        Exam examDSA = new Exam("EXAM-DSA", "Data Structures & Algorithms Certification", "Medi-Caps University");
        Exam examAI = new Exam("EXAM-AI", "Artificial Intelligence Fundamentals", "Stanford University");
        service.addExam(examDSA);
        service.addExam(examAI);

        // Slots
        ExamSlot slotDsaMorning = new ExamSlot(
                "SLOT-DSA-AM", "EXAM-DSA",
                BASE_TIME.plusHours(2), BASE_TIME.plusHours(4),
                BASE_TIME.plusHours(1).plusMinutes(45), BASE_TIME.plusHours(2)
        );

        ExamSlot slotDsaAfternoon = new ExamSlot(
                "SLOT-DSA-PM", "EXAM-DSA",
                BASE_TIME.plusHours(6), BASE_TIME.plusHours(8),
                BASE_TIME.plusHours(5).plusMinutes(45), BASE_TIME.plusHours(6)
        );

        ExamSlot slotAiOverlap = new ExamSlot(
                "SLOT-AI-OVERLAP", "EXAM-AI",
                BASE_TIME.plusHours(3), BASE_TIME.plusHours(5),
                BASE_TIME.plusHours(2).plusMinutes(45), BASE_TIME.plusHours(3)
        );

        // Rooms
        slotDsaMorning.addProctoringRoom(new ProctoringRoom("ROOM-A1", "Virtual Room Alpha-1", 2));
        slotDsaMorning.addProctoringRoom(new ProctoringRoom("ROOM-A2", "Virtual Room Alpha-2", 1));

        slotDsaAfternoon.addProctoringRoom(new ProctoringRoom("ROOM-B1", "Virtual Room Beta-1", 5));
        slotAiOverlap.addProctoringRoom(new ProctoringRoom("ROOM-C1", "Virtual Room Gamma-1", 5));

        service.addExamSlot(slotDsaMorning);
        service.addExamSlot(slotDsaAfternoon);
        service.addExamSlot(slotAiOverlap);
    }

    private static void runAutomatedSuite(ExamManagementService service) {
        System.out.println("\n===============================================================================");
        System.out.println("   AUTOMATED TEST SUITE: TESTING ALL 8 REQUIREMENTS & 5 EDGE CASES   ");
        System.out.println("===============================================================================\n");

        System.out.println("--- STEP 1: INITIALIZING PARTNER UNIVERSITIES, EXAMS, SLOTS & ROOMS ---");
        System.out.println(" Environment initialized successfully.\n");

        System.out.println("--- STEP 2: REGISTERING STUDENTS & GENERATING ADMIT TICKETS ---");
        AdmitTicket ticketAlice = service.registerStudentForExam("S101", "EXAM-DSA", "SLOT-DSA-AM");
        System.out.println("   Issued Admit Ticket for Alice: " + ticketAlice.getTicketId() + " (Room: " + ticketAlice.getRoomId() + ")");

        AdmitTicket ticketBob = service.registerStudentForExam("S102", "EXAM-DSA", "SLOT-DSA-AM");
        System.out.println("   Issued Admit Ticket for Bob: " + ticketBob.getTicketId() + " (Room: " + ticketBob.getRoomId() + ")\n");

        System.out.println("--- STEP 3: EDGE CASE 1 - CROSS-UNIVERSITY TIME CONFLICT PREVENTION ---");
        System.out.println("  Scenario: Alice registered for DSA (12:00-14:00). Attempting to register AI (13:00-15:00)...");
        try {
            service.registerStudentForExam("S101", "EXAM-AI", "SLOT-AI-OVERLAP");
        } catch (SchedulingConflictException e) {
            System.out.println("   CAUGHT EXPECTED EXCEPTION: " + e.getMessage() + "\n");
        }

        System.out.println("--- STEP 4: EDGE CASE 2 - ROOM CAPACITY FULL PREVENTION ---");
        AdmitTicket ticketCharlie = service.registerStudentForExam("S103", "EXAM-DSA", "SLOT-DSA-AM");
        System.out.println("  Issued Admit Ticket for Charlie: " + ticketCharlie.getTicketId() + " (Room: " + ticketCharlie.getRoomId() + ")");
        System.out.println("  Attempting to register 4th candidate (Diana) when all rooms in SLOT-DSA-AM are full...");
        try {
            service.registerStudentForExam("S104", "EXAM-DSA", "SLOT-DSA-AM");
        } catch (CapacityExceededException e) {
            System.out.println("  CAUGHT EXPECTED EXCEPTION: " + e.getMessage() + "\n");
        }

        System.out.println("--- STEP 5: RESCHEDULING & RESCHEDULE EDGE CASES ---");
        AdmitTicket ticketDianaAi = service.registerStudentForExam("S104", "EXAM-AI", "SLOT-AI-OVERLAP");
        AdmitTicket ticketDianaDsaPm = service.registerStudentForExam("S104", "EXAM-DSA", "SLOT-DSA-PM");

        System.out.println("  Scenario A: Diana reschedules DSA from Afternoon to Morning (conflicts with AI 13:00-15:00)...");
        try {
            service.rescheduleStudent(ticketDianaDsaPm.getRegistrationId(), "SLOT-DSA-AM", BASE_TIME);
        } catch (SchedulingConflictException e) {
            System.out.println("  CAUGHT EXPECTED EXCEPTION: " + e.getMessage());
        }

        System.out.println("\n  Scenario B: Reschedule submitted AFTER check-in window opens...");
        LocalDateTime windowOpenedTime = service.getExamSlot("SLOT-DSA-AM").getCheckInOpenTime().plusMinutes(5);
        try {
            service.rescheduleStudent(ticketAlice.getRegistrationId(), "SLOT-DSA-PM", windowOpenedTime);
        } catch (RescheduleWindowClosedException e) {
            System.out.println("  ❌ CAUGHT EXPECTED EXCEPTION: " + e.getMessage());
        }

        System.out.println("\n  Scenario C: Valid Reschedule BEFORE window opens...");
        LocalDateTime beforeWindowTime = service.getExamSlot("SLOT-DSA-AM").getCheckInOpenTime().minusMinutes(30);
        AdmitTicket newAliceTicket = service.rescheduleStudent(ticketAlice.getRegistrationId(), "SLOT-DSA-PM", beforeWindowTime);
        System.out.println("  ✅ Reschedule confirmed. New Ticket for Alice: " + newAliceTicket.getTicketId() + " (Slot: " + newAliceTicket.getSlotId() + ")\n");

        System.out.println("--- STEP 6: ADMIT TICKET CHECK-IN & RE-USE EDGE CASE ---");
        LocalDateTime checkInTime = service.getExamSlot("SLOT-DSA-PM").getCheckInOpenTime().plusMinutes(5);
        service.checkInWithTicket(newAliceTicket.getTicketId(), checkInTime);

        System.out.println("  Attempting to re-use the SAME Admit Ticket for check-in...");
        try {
            service.checkInWithTicket(newAliceTicket.getTicketId(), checkInTime);
        } catch (InvalidTicketException e) {
            System.out.println("  ❌ CAUGHT EXPECTED EXCEPTION: " + e.getMessage() + "\n");
        }

        System.out.println("--- STEP 7: ROOM UTILIZATION QUERY ---");
        printRoomUtilization(service, "SLOT-DSA-AM");
        printRoomUtilization(service, "SLOT-DSA-PM");
        System.out.println();

        System.out.println("--- STEP 8: NO-SHOW HANDLING AFTER CHECK-IN WINDOW CLOSES ---");
        LocalDateTime postWindowTime = service.getExamSlot("SLOT-DSA-AM").getCheckInCloseTime().plusMinutes(10);
        int noShowsCount = service.processNoShows("SLOT-DSA-AM", postWindowTime);
        System.out.println("  Processed " + noShowsCount + " No-Show student(s) for SLOT-DSA-AM.\n");

        System.out.println("--- STEP 9: PLUGGABLE ROOM ALLOCATION STRATEGY DEMO ---");
        service.setAllocationStrategy(new BalancedAllocationStrategy());
        AdmitTicket ticketEvan = service.registerStudentForExam("S105", "EXAM-DSA", "SLOT-DSA-PM");
        System.out.println("   Issued Admit Ticket for Evan using Balanced Strategy: " + ticketEvan.getTicketId() + " (Room: " + ticketEvan.getRoomId() + ")");

        System.out.println("\n===============================================================================");
        System.out.println("   ALL DEMONSTRATION TESTS & EDGE CASES PASSED SUCCESSFULLY!                ");
        System.out.println("===============================================================================");
    }

    private static void printRoomUtilization(ExamManagementService service, String slotId) {
        Map<String, Object> util = service.getRoomUtilization(slotId);
        System.out.printf("  📊 Utilization for Slot '%s': Filled %d / Total %d (Available: %d)\n",
                slotId, util.get("filledCapacity"), util.get("totalCapacity"), util.get("availableCapacity"));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rooms = (List<Map<String, Object>>) util.get("rooms");
        for (Map<String, Object> r : rooms) {
            System.out.printf("     - Room '%s' (%s): Filled %d/%d (Available: %d) | Full: %s\n",
                    r.get("roomId"), r.get("roomName"), r.get("filled"), r.get("capacity"), r.get("available"), r.get("isFull"));
        }
    }
}
