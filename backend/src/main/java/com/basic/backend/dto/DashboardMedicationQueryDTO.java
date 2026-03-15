package com.basic.backend.dto;

import lombok.Data;

@Data
public class DashboardMedicationQueryDTO {

    private Long medicationId;

    private String name;

    private String dosage;

    private Integer expectedDosesPerDay;

    private Integer takenToday;

}