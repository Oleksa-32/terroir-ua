package com.example.backend.service.user;

import com.example.backend.dto.user.UpdateUserProfileRequestDto;
import com.example.backend.dto.user.UpdateUserRoleRequestDto;
import com.example.backend.dto.user.UserRegistrationRequestDto;
import com.example.backend.dto.user.UserResponseDto;
import com.example.backend.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;

    UserResponseDto updateRole(Long userId, UpdateUserRoleRequestDto dto);

    UserResponseDto getProfile(String email);

    UserResponseDto updateProfile(String email, UpdateUserProfileRequestDto dto);

    UserResponseDto registerOrUpdateGoogleUser(String email, String name);
}
