package dev.resume.backend.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String mesage) {
        super(mesage);
    }
}
