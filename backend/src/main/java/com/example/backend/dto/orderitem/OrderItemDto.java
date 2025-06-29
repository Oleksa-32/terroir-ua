package com.example.backend.dto.orderitem;

import java.math.BigDecimal;

public record OrderItemDto(
        Long id,
        Long wineId,
        int quantity,
        BigDecimal pricePerUnit
) {}
