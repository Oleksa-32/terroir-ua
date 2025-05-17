package com.example.backend.dto.user;

import com.example.backend.model.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateUserRoleRequestDto {
    @NotNull
    private Role.Roles role;
}
