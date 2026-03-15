package com.basic.backend.controller;

import com.basic.backend.common.Result;
import com.basic.backend.dto.DashboardMedicationDTO;
import com.basic.backend.dto.DashboardOverviewDTO;
import com.basic.backend.dto.MedicationLogRequestDTO;
import com.basic.backend.service.DashboardService;
import com.basic.backend.util.SecurityUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/overview")
    public Result<DashboardOverviewDTO> getOverview() {
        Long userId = SecurityUtil.getUserId();
        DashboardOverviewDTO data = dashboardService.getOverview(userId);
        return Result.ok(data);
    }

    @PostMapping("/medication/log")
    public Result<DashboardMedicationDTO> logMedicationDose(@RequestBody MedicationLogRequestDTO request) {
        Long userId = SecurityUtil.getUserId();
        DashboardMedicationDTO data = dashboardService.logMedicationDose(userId, request.getMedicationId());
        return Result.ok(data);
    }
}