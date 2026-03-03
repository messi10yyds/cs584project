package com.basic.backend.entity;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String username;
    private String passwordHash;
}