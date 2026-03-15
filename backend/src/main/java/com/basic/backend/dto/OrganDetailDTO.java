package com.basic.backend.dto;

import lombok.Data;

@Data
public class OrganDetailDTO {
    private String organ;
    private DashboardScreeningDTO screening;
    private OrganEducationDTO education;
}