package com.basic.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Medication {
    private Long id;
    private Long userId;

    private String name;
    private Boolean isActive;
    private String dosage;
    private Integer expectedDosesPerDay;

    private LocalDateTime createdAt;
}