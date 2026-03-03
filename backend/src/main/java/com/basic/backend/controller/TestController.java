package com.basic.backend.controller;

import com.basic.backend.common.Result;
import com.basic.backend.util.SecurityUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test/me")
    public Result<?> me() {
        Long userId = SecurityUtil.getUserId();
        return Result.ok(userId);
    }
}