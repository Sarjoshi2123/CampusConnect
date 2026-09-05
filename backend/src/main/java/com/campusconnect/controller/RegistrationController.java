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

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody RegistrationRequest request) {
        RegistrationResult result = registrationService.register(request.getStudentId(), request.getExamSlotId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    @PostMapping("/{registrationId}/reschedule")
    public ResponseEntity<RegistrationResponse> reschedule(@PathVariable String registrationId,
                                                             @Valid @RequestBody RescheduleRequest request) {
        RegistrationResult result = registrationService.reschedule(registrationId, request.getNewExamSlotId());
        return ResponseEntity.ok(toResponse(result));
    }

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
