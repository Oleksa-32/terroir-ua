package com.example.backend.security;

import com.example.backend.dto.user.UserLoginRequestDto;
import com.example.backend.dto.user.UserLoginResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;

    public UserLoginResponseDto authenticate(UserLoginRequestDto userLoginRequestDto) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginRequestDto.email(),
                        userLoginRequestDto.password())
        );
        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User has no role"));

        String token = jwtUtil.generateToken(auth.getName(), role);

        return new UserLoginResponseDto()
                .setToken(token)
                .setRole(role);
    }
}
