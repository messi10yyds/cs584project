package com.basic.backend.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DashboardScreeningQueryDTO {

    private Long userScreeningId;

    private Long screeningTypeId;

    private String screeningType;

    private LocalDate lastCompletedDate;

    private Integer recommendedIntervalDays;

}