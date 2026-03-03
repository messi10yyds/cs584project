package com.basic.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HealthProfile {
    private Long id;
    private Long userId;

    private Integer age;
    private String gender;
    private String diabetesType;
    private String diagnosisTimeframe;

    private LocalDateTime createdAt;
}