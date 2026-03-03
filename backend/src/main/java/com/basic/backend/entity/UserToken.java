package com.basic.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserToken {
    private Long userId;
    private String token;
    private LocalDateTime expireAt;
}