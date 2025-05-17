package com.example.backend.controller;

import com.example.backend.dto.user.UserLoginRequestDto;
import com.example.backend.dto.user.UserLoginResponseDto;
import com.example.backend.dto.user.UserRegistrationRequestDto;
import com.example.backend.dto.user.UserResponseDto;
import com.example.backend.exception.RegistrationException;
import com.example.backend.security.AuthenticationService;
import com.example.backend.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "Endpoints for user registration")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationService authService;

    @PostMapping("registration")
    @Operation(summary = "Register a new user",
            description = "Creates a new user account using the provided registration details")
    public UserResponseDto registerUser(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }

    @PostMapping("login")
    @Operation(
            summary = "Login an authenticated user",
            description = "Login an authenticated user"
    )
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authService.authenticate(requestDto);
    }
}
