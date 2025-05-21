package dev.resume.backend.services;

import dev.resume.backend.dto.CreateUserResponseDTO;
import dev.resume.backend.dto.UserRequestDTO;
import dev.resume.backend.dto.UserResponseDTO;
import dev.resume.backend.entities.UserEntity;
import dev.resume.backend.exceptions.*;
import dev.resume.backend.mappers.UserMapper;
import dev.resume.backend.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO getUserById(UUID id) {
        UserEntity user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User with id " + id + " not found"));

        return userMapper.toResponse(user);
    }

    public CreateUserResponseDTO create(UserRequestDTO userDTO) {
        if (userRepository.findByEmail(userDTO.email()) != null) {
            throw new UserAlreadyExistException();
        }

        UserEntity user = userMapper.toEntity(userDTO);

        String verificationCode = generateVerificationCode();
        user.setVerificationCode(verificationCode);
        user.setVerificationCodeCreatedAt(LocalDateTime.now().plusMinutes(10));
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        UserEntity savedUser = userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), verificationCode);

        String message = "User created successfully. Please verify your email using the code sent to " + user.getEmail() +
                ". The code will expire in 10 minutes.";
        return userMapper.toCreateResponse(savedUser, message);
    }

    public UserResponseDTO update(UUID id, UserRequestDTO userDTO) {
        UserEntity user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User with id " + id + " not found"));

        userMapper.updateEntityFromDto(userDTO, user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        UserEntity updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    public void delete(UUID id) {
        UserEntity user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User with id " + id + " not found"));

        userRepository.delete(user);
    }

    public String verifyEmail(String email, String code) {
        UserEntity user = userRepository.findByEmail(email);
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


        user.setEmailVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeCreatedAt(null);

        userRepository.save(user);

        return String.format("Email %s verified successfully", email);
    }

    private String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

}