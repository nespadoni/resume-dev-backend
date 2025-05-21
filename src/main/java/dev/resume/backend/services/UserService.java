package dev.resume.backend.services;

import dev.resume.backend.dto.UserRequestDTO;
import dev.resume.backend.dto.UserResponseDTO;
import dev.resume.backend.entities.UserEntity;
import dev.resume.backend.exceptions.UserAlreadyExistException;
import dev.resume.backend.mappers.UserMapper;
import dev.resume.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponseDTO getUserById(UUID id) {
        UserEntity user = userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("User with id " + id + " not found"));

        return userMapper.toResponse(user);
    }

    public UserResponseDTO create(UserRequestDTO userDTO) {
        if (userRepository.findByEmail(userDTO.email()) != null) {
            throw new UserAlreadyExistException();
        }

        UserEntity user = userMapper.toEntity(userDTO);
        UserEntity savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }


}