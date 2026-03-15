package com.basic.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrganEducationDTO {
    private String title;
    private List<WhatIsItDTO> whatIsIt;
    private List<RiskFactorDTO> riskFactors;
    private List<String> checkups;
}