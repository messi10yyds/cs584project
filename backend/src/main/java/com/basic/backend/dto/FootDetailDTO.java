package com.basic.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FootDetailDTO {

    private String organ;
    private FootEducationDTO education;
    private List<FootQuestionDTO> questions;
    private Boolean todayCompleted;
    private List<FootAnswerDTO> todayAnswers;
    private FootCheckResultDTO latestResult;
}