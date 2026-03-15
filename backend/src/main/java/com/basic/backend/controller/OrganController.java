package com.basic.backend.controller;

import com.basic.backend.common.Result;
import com.basic.backend.dto.OrganDetailDTO;
import com.basic.backend.service.OrganService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/organs")
@RequiredArgsConstructor
public class OrganController {

    private final OrganService organService;

    @GetMapping("/{organ}")
    public Result<OrganDetailDTO> getOrganDetail(@PathVariable String organ) {
        return Result.ok(organService.getOrganDetail(organ));
    }
}