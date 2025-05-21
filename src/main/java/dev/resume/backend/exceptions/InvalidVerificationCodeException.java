package dev.resume.backend.exceptions;

public class InvalidVerificationCodeException extends RuntimeException {
    public InvalidVerificationCodeException() {
        super("Code is invalid.");
    }
}
