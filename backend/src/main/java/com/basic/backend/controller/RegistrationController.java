package com.basic.backend.controller;

import com.basic.backend.common.Result;
import com.basic.backend.dto.InitRegistrationRequestDTO;
import com.basic.backend.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
@RestController
@RequestMapping("/api/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
    @PostMapping("/init")
    public Result<?> init(@Valid @RequestBody InitRegistrationRequestDTO req) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userIdStr = (String) auth.getPrincipal(); // 你的 principal 就是 subject=userId :contentReference[oaicite:1]{index=1}
        Long userId = Long.valueOf(userIdStr);

        registrationService.initProfile(userId, req);
        return Result.ok("OK");
    }
}