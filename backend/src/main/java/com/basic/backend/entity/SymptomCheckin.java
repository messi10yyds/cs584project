package com.basic.backend.entity;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SymptomCheckin {

    private Long id;
    private Long userId;
    private Long questionId;
    private Boolean answer;
    private LocalDate checkinDate;
}