package dev.resume.backend.dto;

public record CreateUserResponseDTO(
        UserResponseDTO user,
        String message
) {
}
