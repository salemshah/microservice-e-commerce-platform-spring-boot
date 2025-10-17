package com.ecommerce.auth.mapper;

import com.ecommerce.auth.dto.RegisterRequest;
import com.ecommerce.auth.dto.AuthResponse;
import com.ecommerce.auth.dto.UserResponse;
import com.ecommerce.auth.entity.Role;
import com.ecommerce.auth.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // Map from RegisterRequest -> User entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "verified", constant = "false")
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    User toEntity(RegisterRequest dto);

    // Map from User -> AuthResponse
    @Mapping(target = "token", ignore = true)
    AuthResponse toAuthResponse(User user);

    // Map from User -> UserResponse
    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRolesToStrings")
    UserResponse toUserResponse(User user);

    @Named("mapRolesToStrings")
    default Set<String> mapRolesToStrings(Set<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}
