package com.example.backend.service.shoppingcart;

import com.example.backend.dto.cartitem.UpdateCartItemDto;
import com.example.backend.dto.shoppingcart.CreateCartItemDto;
import com.example.backend.dto.shoppingcart.ShoppingCartDto;
import com.example.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShoppingCartService {
    ShoppingCartDto findByUserId(Long userId);

    Page<ShoppingCartDto> findAllUserCarts(Pageable pageable);

    ShoppingCartDto addItemToCart(CreateCartItemDto createCartItemDto, Long userId);

    void deleteById(Long cartItemId);

    void createShoppingCartForUser(User user);

    ShoppingCartDto updateQuantity(Long cartItemId, UpdateCartItemDto updateCartItemDto);
}
