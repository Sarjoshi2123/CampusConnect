package com.campusconnect.controller;

import com.campusconnect.common.dto.ExamSlotCreateRequest;
import com.campusconnect.common.dto.ExamSlotResponse;
import com.campusconnect.common.dto.RoomUtilizationResponse;
import com.campusconnect.model.ExamSlot;
import com.campusconnect.service.ExamSlotService;
import com.campusconnect.service.NoShowService;
import com.campusconnect.service.RoomUtilizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ExamSlotController {

    private final ExamSlotService examSlotService;
    private final RoomUtilizationService roomUtilizationService;
    private final NoShowService noShowService;

    public ExamSlotController(ExamSlotService examSlotService,
                               RoomUtilizationService roomUtilizationService,
                               NoShowService noShowService) {
        this.examSlotService = examSlotService;
        this.roomUtilizationService = roomUtilizationService;
        this.noShowService = noShowService;
    }

    @PostMapping("/exams/{examId}/slots")
    public ResponseEntity<ExamSlotResponse> createSlot(@PathVariable String examId,
                                                        @RequestBody ExamSlotCreateRequest request) {
        ExamSlot slot = examSlotService.createSlot(
                examId, request.getDate(), request.getStartTime(), request.getDurationMinutes());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(slot));
    }

    @GetMapping("/exams/{examId}/slots")
    public ResponseEntity<List<ExamSlotResponse>> listSlotsForExam(@PathVariable String examId) {
        List<ExamSlotResponse> responses = examSlotService.listSlotsForExam(examId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/slots/{slotId}")
    public ResponseEntity<ExamSlotResponse> getSlot(@PathVariable String slotId) {
        return ResponseEntity.ok(toResponse(examSlotService.getSlot(slotId)));
    }

    @GetMapping("/slots/{slotId}/utilization")
    public ResponseEntity<List<RoomUtilizationResponse>> getUtilization(@PathVariable String slotId) {
        return ResponseEntity.ok(roomUtilizationService.getUtilizationForSlot(slotId));
    }

    @PostMapping("/slots/{slotId}/no-shows/process")
    public ResponseEntity<Map<String, Integer>> processNoShows(@PathVariable String slotId) {
        int marked = noShowService.markNoShowsForSlot(slotId);
        return ResponseEntity.ok(Map.of("markedNoShow", marked));
    }

    private ExamSlotResponse toResponse(ExamSlot slot) {
        return new ExamSlotResponse(
                slot.getId(), slot.getExamId(), slot.getDate(), slot.getStartTime(),
                slot.getEndTime(), slot.getDurationMinutes());
    }
}

