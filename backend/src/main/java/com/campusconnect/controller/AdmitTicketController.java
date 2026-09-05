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

@RestController
@RequestMapping("/api/admit-tickets")
public class AdmitTicketController {

    private final AdmitTicketService admitTicketService;

    public AdmitTicketController(AdmitTicketService admitTicketService) {
        this.admitTicketService = admitTicketService;
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<AdmitTicketResponse> getTicket(@PathVariable String ticketId) {
        return ResponseEntity.ok(toResponse(admitTicketService.getTicket(ticketId)));
    }

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

