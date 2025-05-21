package dev.resume.backend.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendVerificationEmail(String toEmail, String verificationCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Email Verification");
            message.setText("Please verify your email using the following code: " + verificationCode +
                    "\n This code will expire in 10 minutes.");

            mailSender.send(message);
            log.info("Email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Error sending email to: {}", e.getMessage());
            throw new RuntimeException("Erro ao enviar email de verificação");
        }
    }
}
