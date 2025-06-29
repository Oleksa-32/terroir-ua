package com.example.backend.dto.cartitem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateCartItemDto {
    @NotNull
    private Long wineId;
    @Positive(message = "Quantity can't be less than 0")
    private int quantity;
}
