package com.example.backend.dto.shoppingcart;

import com.example.backend.dto.cartitem.CartItemDto;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ShoppingCartDto {
    private Long userId;
    private Set<CartItemDto> cartItems = new HashSet<>();
    private BigDecimal amount;
    private BigDecimal deliveryPrice;
    private BigDecimal totalPrice;
}
