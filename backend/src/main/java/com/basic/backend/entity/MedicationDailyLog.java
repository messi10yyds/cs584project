package com.basic.backend.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MedicationDailyLog {
    private Long id;
    private Long medicationId;
    private Long userId;
    private LocalDate logDate;
    private Integer dosesTaken;
    private LocalDateTime createdAt;
}