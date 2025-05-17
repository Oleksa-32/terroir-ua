package com.example.backend.dto.user;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserResponseDto {
    private Long id;
    private String email;
    private String name;

}
