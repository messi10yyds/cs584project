package com.basic.backend.entity;

import lombok.Data;

@Data
public class ScreeningType {
    private Long id;
    private String name;
    private Integer isActive;
    private Integer recommendedIntervalDays;
}