package com.basic.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDTO {

    @NotBlank(message = "username is required")
    @Size(min = 3, max = 50, message = "username length must be 3-50")
    private String username;

    @NotBlank(message = "password is required")
    @Size(min = 6, max = 100, message = "password length must be 6-100")
    private String password;
}