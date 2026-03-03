package com.basic.backend.controller;

import com.basic.backend.common.Result;
import com.basic.backend.dto.InitStatusResponse;
import com.basic.backend.mapper.HealthProfileMapper;
import com.basic.backend.mapper.MedicationMapper;
import com.basic.backend.mapper.UserScreeningMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/init")
@RequiredArgsConstructor
public class InitController {

    private final HealthProfileMapper healthProfileMapper;
    private final UserScreeningMapper userScreeningMapper;
    private final MedicationMapper medicationMapper;

    @GetMapping("/status")
    public Result<?> initStatus() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userIdStr = (String) auth.getPrincipal();
        Long userId = Long.valueOf(userIdStr);

        boolean profile = healthProfileMapper.selectByUserId(userId) != null;
        boolean screenings = userScreeningMapper.countByUserId(userId) > 0;
        boolean medications = medicationMapper.countByUserId(userId) > 0;

        boolean initialized = profile && screenings; // meds 不强制

        return Result.ok(new InitStatusResponse(initialized, profile, screenings, medications));
    }
}