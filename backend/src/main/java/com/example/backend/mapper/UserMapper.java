package com.example.backend.mapper;

import com.example.backend.config.MapperConfig;
import com.example.backend.dto.user.UpdateUserProfileRequestDto;
import com.example.backend.dto.user.UserRegistrationRequestDto;
import com.example.backend.dto.user.UserResponseDto;
import com.example.backend.model.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto userRegistrationRequestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProfileFromDto(UpdateUserProfileRequestDto dto, @MappingTarget User user);
}
