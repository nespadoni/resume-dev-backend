package dev.resume.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<?> handleUserAlreadyExists(UserAlreadyExistException e) {
        return buildResponse(HttpStatus.CONFLICT, "User already exists", e.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, "Resource not found", e.getMessage());
    }

    @ExceptionHandler(VerificationCodeExpiredException.class)
    public ResponseEntity<?> handleCodeExpired(VerificationCodeExpiredException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Verification code expired", e.getMessage());
    }

    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ResponseEntity<?> handleInvalidCode(InvalidVerificationCodeException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid verification code", e.getMessage());
    }

    @ExceptionHandler(EmailAlreadyVerifiedException.class)
    public ResponseEntity<?> handleEmailAlreadyVerified(EmailAlreadyVerifiedException e) {
        return buildResponse(HttpStatus.CONFLICT, "Email already verified", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception e) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", e.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String error, String message) {
        return ResponseEntity.status(status).body(
                Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "status", status.value(),
                        "error", error,
                        "message", message
                )
        );
    }
}
