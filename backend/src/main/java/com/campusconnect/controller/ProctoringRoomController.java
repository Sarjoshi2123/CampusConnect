package com.campusconnect.controller;

import com.campusconnect.common.dto.ProctoringRoomCreateRequest;
import com.campusconnect.common.dto.ProctoringRoomResponse;
import com.campusconnect.model.ProctoringRoom;
import com.campusconnect.service.ProctoringRoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/slots/{slotId}/rooms")
public class ProctoringRoomController {

    private final ProctoringRoomService proctoringRoomService;

    public ProctoringRoomController(ProctoringRoomService proctoringRoomService) {
        this.proctoringRoomService = proctoringRoomService;
    }

    @PostMapping
    public ResponseEntity<ProctoringRoomResponse> createRoom(@PathVariable String slotId,
                                                              @RequestBody ProctoringRoomCreateRequest request) {
        ProctoringRoom room = proctoringRoomService.createRoom(slotId, request.getCapacity());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(room));
    }

    @GetMapping
    public ResponseEntity<List<ProctoringRoomResponse>> listRoomsForSlot(@PathVariable String slotId) {
        List<ProctoringRoomResponse> responses = proctoringRoomService.listRoomsForSlot(slotId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    private ProctoringRoomResponse toResponse(ProctoringRoom room) {
        return new ProctoringRoomResponse(
                room.getId(), room.getExamSlotId(), room.getCapacity(),
                room.getCurrentOccupancy(), room.getAvailableCapacity());
    }
}
