package com.campusconnect.controller;

import com.campusconnect.common.dto.RegistrationRequest;
import com.campusconnect.common.dto.RegistrationResponse;
import com.campusconnect.common.dto.RescheduleRequest;
import com.campusconnect.model.Registration;
import com.campusconnect.service.RegistrationResult;
import com.campusconnect.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Purpose: REST endpoints for registering students into exam slots, rescheduling
 *          existing registrations, and retrieving a registration by ID.
 * Role: Controller layer — delegates all business logic to RegistrationService
 *       and only ever returns RegistrationResponse DTOs, never the Registration
 *       or AdmitTicket domain entities.
 */
@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    /**
     * Registers a student into an exam slot and issues an admit ticket.
     * @param request the student and target exam slot.
     * @return the new registration (with its issued admit ticket ID), as a
     *         RegistrationResponse.
     */
    @PostMapping
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody RegistrationRequest request) {
        RegistrationResult result = registrationService.register(request.getStudentId(), request.getExamSlotId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    /**
     * Reschedules an existing registration to a different exam slot.
     * @param registrationId the registration to move.
     * @param request        the target exam slot.
     * @return the new registration (with its freshly issued admit ticket ID), as
     *         a RegistrationResponse.
     */
    @PostMapping("/{registrationId}/reschedule")
    public ResponseEntity<RegistrationResponse> reschedule(@PathVariable String registrationId,
                                                            @Valid @RequestBody RescheduleRequest request) {
        RegistrationResult result = registrationService.reschedule(registrationId, request.getNewExamSlotId());
        return ResponseEntity.ok(toResponse(result));
    }

    /**
     * Retrieves a registration by ID.
     * @param registrationId the registration ID.
     * @return the registration, as a RegistrationResponse (admitTicketId omitted;
     *         look it up via the admit ticket API if needed).
     */
    @GetMapping("/{registrationId}")
    public ResponseEntity<RegistrationResponse> getRegistration(@PathVariable String registrationId) {
        Registration registration = registrationService.getRegistration(registrationId);
        return ResponseEntity.ok(toResponse(registration, null));
    }

    private RegistrationResponse toResponse(RegistrationResult result) {
        return toResponse(result.getRegistration(), result.getAdmitTicket().getId());
    }

    private RegistrationResponse toResponse(Registration registration, String admitTicketId) {
        return new RegistrationResponse(
                registration.getId(), registration.getStudentId(), registration.getExamId(),
                registration.getExamSlotId(), registration.getProctoringRoomId(),
                registration.getRegistrationTime(), registration.getStatus().name(), admitTicketId);
    }
}
