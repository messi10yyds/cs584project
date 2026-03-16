package com.basic.backend.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ScreeningAppointment {

    private Long id;

    private Long userId;

    private Long screeningTypeId;

    private LocalDate scheduledFor;

    private String status;

    private LocalDateTime createdAt;
}