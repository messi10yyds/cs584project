package com.basic.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FootAnswerDTO {

    private Long questionId;
    private Boolean answer;
}