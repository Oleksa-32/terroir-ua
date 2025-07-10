package com.example.backend.dto.user;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserLoginResponseDto {
    private String token;
    private String role;
}
