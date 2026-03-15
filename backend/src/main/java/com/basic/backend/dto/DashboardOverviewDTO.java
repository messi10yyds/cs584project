package com.basic.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardOverviewDTO {

    private DashboardGreetingDTO greeting;

    private List<DashboardOrganDTO> organs;

    private List<DashboardRiskAlertDTO> riskAlerts;

    private List<DashboardMedicationDTO> todayMedications;

    private List<DashboardScreeningDTO> screenings;

}