package com.basic.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardRiskAlertDTO {

    private String type;

    private String level;

    private String title;

    private String message;

    private String relatedKey;

    private String actionType;

}