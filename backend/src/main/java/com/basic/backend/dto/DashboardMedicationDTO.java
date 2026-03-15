package com.basic.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMedicationDTO {

    private Long medicationId;

    private String name;

    private String dosageText;

    private Integer expectedDosesPerDay;

    private Integer takenToday;

    private String status;

}