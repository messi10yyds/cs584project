package com.basic.backend.controller;

import com.basic.backend.common.Result;
import com.basic.backend.dto.LoginRequestDTO;
import com.basic.backend.dto.RegisterRequestDTO;
import com.basic.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginRequestDTO req) {

        return Result.ok(
                authService.login(
                        req.getUsername(),
                        req.getPassword()
                )
        );
    }

    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterRequestDTO req) {
        return Result.ok(authService.register(req.getUsername(), req.getPassword()));
    }
}