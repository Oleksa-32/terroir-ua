package com.example.backend.dto.cartitem;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateCartItemDto {
    @Positive
    private int quantity;
}
