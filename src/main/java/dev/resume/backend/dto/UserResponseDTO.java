package dev.resume.backend.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String name,
        String email,
        boolean emailVerified,
        LocalDateTime createdAt
) {
}