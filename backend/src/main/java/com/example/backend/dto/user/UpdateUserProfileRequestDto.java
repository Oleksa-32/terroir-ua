package com.example.backend.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateUserProfileRequestDto {
    @Size(max = 50)
    private String name;
}
