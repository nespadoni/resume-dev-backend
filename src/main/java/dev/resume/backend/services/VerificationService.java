package dev.resume.backend.services;

import dev.resume.backend.entities.UserEntity;
import dev.resume.backend.exceptions.*;
import dev.resume.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class VerificationService {
    private final UserRepository userRepository;
    private final EmailService emailService;

    public String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    public void sendVerificationEmail(UserEntity user, String verificationCode) {
        emailService.sendVerificationEmail(user.getEmail(), verificationCode);
    }

    public void setupVerificationCode(UserEntity user) {
        String verificationCode = generateVerificationCode();
        user.setVerificationCode(verificationCode);
        user.setVerificationCodeCreatedAt(LocalDateTime.now().plusMinutes(10));
    }

    public String verifyEmail(String email, String code) {
        UserEntity user = userRepository.findByEmail(email);
        validateVerification(user, email, code);

        user.setEmailVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeCreatedAt(null);

        userRepository.save(user);

        return String.format("Email %s verified successfully", email);
    }

    private void validateVerification(UserEntity user, String email, String code) {
        if (user == null) {
            throw new ResourceNotFoundException("User with email " + email + " not found");
        }

        if (user.getVerificationCode() == null) {
            throw new VerificationCodeNotFoundException();
        }

        if (!user.getVerificationCode().equals(code)) {
            throw new InvalidVerificationCodeException();
        }

        if (user.getVerificationCodeCreatedAt().isBefore(LocalDateTime.now())) {
            throw new VerificationCodeExpiredException();
        }

        if (user.isEmailVerified()) {
            throw new EmailAlreadyVerifiedException();
        }
    }
}