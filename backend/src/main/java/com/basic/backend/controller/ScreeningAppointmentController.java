package com.basic.backend.controller;

import com.basic.backend.dto.ScreeningAppointmentQueryDTO;
import com.basic.backend.dto.ScreeningAppointmentRequestDTO;
import com.basic.backend.service.ScreeningAppointmentService;
import com.basic.backend.common.Result;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/screenings")
public class ScreeningAppointmentController {

    private final ScreeningAppointmentService screeningAppointmentService;

    public ScreeningAppointmentController(ScreeningAppointmentService screeningAppointmentService) {
        this.screeningAppointmentService = screeningAppointmentService;
    }

    @GetMapping("/{screeningTypeId}/appointment")
    public Result<ScreeningAppointmentQueryDTO> getAppointmentPage(@PathVariable Long screeningTypeId) {
        Long userId = getCurrentUserId();
        ScreeningAppointmentQueryDTO data =
                screeningAppointmentService.getAppointmentPage(userId, screeningTypeId);
        return Result.ok(data);
    }

    @PostMapping("/{screeningTypeId}/appointment")
    public Result<?> bookAppointment(@PathVariable Long screeningTypeId,
                                     @RequestBody ScreeningAppointmentRequestDTO request) {
        Long userId = getCurrentUserId();
        screeningAppointmentService.bookOrReschedule(userId, screeningTypeId, request);
        return Result.ok(null);
    }

    @PutMapping("/{screeningTypeId}/appointment/reschedule")
    public Result<?> rescheduleAppointment(@PathVariable Long screeningTypeId,
                                           @RequestBody ScreeningAppointmentRequestDTO request) {
        Long userId = getCurrentUserId();
        screeningAppointmentService.bookOrReschedule(userId, screeningTypeId, request);
        return Result.ok(null);
    }

    @PutMapping("/{screeningTypeId}/appointment/complete")
    public Result<?> completeAppointment(@PathVariable Long screeningTypeId) {
        Long userId = getCurrentUserId();
        screeningAppointmentService.completeAppointment(userId, screeningTypeId);
        return Result.ok(null);
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userIdStr = (String) auth.getPrincipal();
        return Long.valueOf(userIdStr);
    }
}