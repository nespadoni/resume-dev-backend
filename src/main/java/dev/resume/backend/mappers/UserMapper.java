package dev.resume.backend.mappers;

import dev.resume.backend.dto.UserRequestDTO;
import dev.resume.backend.dto.UserResponseDTO;
import dev.resume.backend.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity toEntity(UserRequestDTO dto);

    void updateEntityFromDto(UserRequestDTO dto, @MappingTarget UserEntity entity);

    UserResponseDTO toResponse(UserEntity entity);
}
