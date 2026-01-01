package fr.lucasbmmn.overhearrserver.user.mapper;

import fr.lucasbmmn.overhearrserver.user.dto.UserCreationRequest;
import fr.lucasbmmn.overhearrserver.user.dto.UserResponse;
import fr.lucasbmmn.overhearrserver.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct interface for converting between User entities and Data Transfer Objects (DTOs).
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    /**
     * Converts a user creation request into a User entity.
     *
     * @param request The {@link UserCreationRequest} containing registration data.
     * @return A transient {@link User} entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    User toEntity(UserCreationRequest request);

    /**
     * Converts a User entity into a safe response DTO.
     *
     * @param user The {@link User} entity.
     * @return The {@link UserResponse} DTO.
     */
    UserResponse toResponse(User user);
}