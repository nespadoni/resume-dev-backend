package dev.resume.backend.dto;

public record VerifyEmailRequestDTO(
        String email,
        String code
) {
}
