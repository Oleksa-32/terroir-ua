package com.example.backend.dto.shoppingcart;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

@Data
public class ShoppingCartDto {
    private Long userId;
    private Set<com.example.backend.dto.shoppingcart.CartItemDto> cartItems = new HashSet<>();
    private BigDecimal amount;
    private BigDecimal deliveryPrice;
    private BigDecimal totalPrice;
}
