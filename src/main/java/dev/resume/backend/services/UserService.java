package dev.resume.backend.services;

import dev.resume.backend.dto.CreateUserResponseDTO;
import dev.resume.backend.dto.UserRequestDTO;
import dev.resume.backend.dto.UserResponseDTO;
import dev.resume.backend.entities.UserEntity;
import dev.resume.backend.exceptions.ResourceNotFoundException;
import dev.resume.backend.exceptions.UserAlreadyExistException;
import dev.resume.backend.mappers.UserMapper;
import dev.resume.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final VerificationService verificationService;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO getUserById(UUID id) {
        return userMapper.toResponse(findUserById(id));
    }

    public CreateUserResponseDTO create(UserRequestDTO userDTO) {
        validateNewUser(userDTO.email());

        UserEntity user = userMapper.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        verificationService.setupVerificationCode(user);
        UserEntity savedUser = userRepository.save(user);
        verificationService.sendVerificationEmail(savedUser, savedUser.getVerificationCode());

        String message = String.format(
                "User created successfully. Please verify your email %s using the code we sent. " +
                        "The code will expire in 10 minutes.", savedUser.getEmail());

        return userMapper.toCreateResponse(savedUser, message);
    }

    public UserResponseDTO update(UUID id, UserRequestDTO userDTO) {
        UserEntity user = findUserById(id);
        userMapper.updateEntityFromDto(userDTO, user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userMapper.toResponse(userRepository.save(user));
    }

    public void delete(UUID id) {
        userRepository.delete(findUserById(id));
    }

    public String verifyEmail(String email, String code) {
        return verificationService.verifyEmail(email, code);
    }

    private UserEntity findUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
    }

    private void validateNewUser(String email) {
        if (userRepository.findByEmail(email) != null) {
            throw new UserAlreadyExistException();
        }
    }
}