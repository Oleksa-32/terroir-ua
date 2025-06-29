package com.example.backend.dto.order;

import com.example.backend.dto.orderitem.OrderItemDto;
import java.math.BigDecimal;
import java.util.Set;

public record OrderDto(
        Long id,
        Set<OrderItemDto> items,
        String firstName,
        String lastName,
        String shippingAddress,
        int zipCode,
        String phoneNumber,
        String details,
        BigDecimal totalPrice
) {}
