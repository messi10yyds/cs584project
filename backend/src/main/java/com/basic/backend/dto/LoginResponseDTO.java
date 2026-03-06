package com.basic.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {

    private Long userId;

    private String token;

    private String tokenType;

}