package com.example.hellospring;

import java.time.LocalDateTime;

// минимальный DTO, без User целиком
public record MessageResponse(
        Long id,
        String content,
        String authorUsername,
        LocalDateTime createdAt
) {
}
