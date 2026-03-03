package com.basic.backend.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserScreening {
    private Long id;
    private Long userId;
    private Integer screeningTypeId;
    private LocalDate lastCompletedDate;
    private LocalDate nextDueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}