package dev.resume.backend.exceptions;

public class VerificationCodeNotFoundException extends RuntimeException {
    public VerificationCodeNotFoundException() {
        super("Verification code not found");
    }
}
