package com.basic.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InitStatusResponseDTO {
    private boolean initialized;
    private boolean profile;
    private boolean screenings;
    private boolean medications;
}