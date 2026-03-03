package com.basic.backend.controller;

import com.basic.backend.common.Result;
import com.basic.backend.dto.LoginRequest;
import com.basic.backend.dto.RegisterRequest;
import com.basic.backend.exception.BizException;
import com.basic.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginRequest req) {

        return Result.ok(
                authService.login(
                        req.getUsername(),
                        req.getPassword()
                )
        );
    }

    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterRequest req) {
        return Result.ok(authService.register(req.getUsername(), req.getPassword()));
    }
}