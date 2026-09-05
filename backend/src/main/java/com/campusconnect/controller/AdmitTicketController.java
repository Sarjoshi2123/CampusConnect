package com.campusconnect.controller;

import com.campusconnect.common.dto.AdmitTicketResponse;
import com.campusconnect.model.AdmitTicket;
import com.campusconnect.service.AdmitTicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Purpose: REST endpoints for retrieving admit tickets and checking them in at
 *          the exam room.
 * Role: Controller layer — delegates all logic to AdmitTicketService and only
 *       ever returns AdmitTicketResponse DTOs, never the domain entity.
 */
@RestController
@RequestMapping("/api/admit-tickets")
public class AdmitTicketController {

    private final AdmitTicketService admitTicketService;

    public AdmitTicketController(AdmitTicketService admitTicketService) {
        this.admitTicketService = admitTicketService;
    }

    /**
     * Retrieves an admit ticket by ID.
     * @param ticketId the admit ticket ID.
     * @return the ticket, as an AdmitTicketResponse.
     */
    @GetMapping("/{ticketId}")
    public ResponseEntity<AdmitTicketResponse> getTicket(@PathVariable String ticketId) {
        return ResponseEntity.ok(toResponse(admitTicketService.getTicket(ticketId)));
    }

    /**
     * Validates and checks in an admit ticket at the exam room, marking it used
     * (single-use) and moving its registration to CHECKED_IN.
     * @param ticketId the admit ticket ID presented by the student.
     * @return the now-used ticket, as an AdmitTicketResponse.
     */
    @PostMapping("/{ticketId}/check-in")
    public ResponseEntity<AdmitTicketResponse> checkIn(@PathVariable String ticketId) {
        return ResponseEntity.ok(toResponse(admitTicketService.checkIn(ticketId)));
    }

    private AdmitTicketResponse toResponse(AdmitTicket ticket) {
        return new AdmitTicketResponse(
                ticket.getId(), ticket.getStudentId(), ticket.getExamId(), ticket.getExamSlotId(),
                ticket.getProctoringRoomId(), ticket.getRegistrationId(), ticket.isUsed());
    }
}
