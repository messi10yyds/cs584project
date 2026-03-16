package com.basic.backend.controller;

import com.basic.backend.common.Result;
import com.basic.backend.dto.FootCheckResultDTO;
import com.basic.backend.dto.FootCheckSubmitDTO;
import com.basic.backend.dto.FootDetailDTO;
import com.basic.backend.service.FootService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/organs/foot")
@RequiredArgsConstructor
public class FootController {

    private final FootService footService;

    @GetMapping
    public Result<FootDetailDTO> getFootDetail() {
        return Result.ok(footService.getFootDetail());
    }

    @PostMapping("/check")
    public Result<FootCheckResultDTO> submitFootCheck(@RequestBody FootCheckSubmitDTO request) {
        return Result.ok(footService.submitFootCheck(request));
    }
}