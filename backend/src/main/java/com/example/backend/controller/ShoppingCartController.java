package com.example.backend.controller;

import com.example.backend.dto.cartitem.CreateCartItemDto;
import com.example.backend.dto.cartitem.UpdateCartItemDto;
import com.example.backend.dto.shoppingcart.ShoppingCartDto;
import com.example.backend.security.SecurityUtil;
import com.example.backend.service.shoppingcart.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService cartService;

    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    @GetMapping
    @Operation(summary = "Get user cart",
            description = "Get cart of currently authorized user")
    public ShoppingCartDto getCart() {
        Long userId = SecurityUtil.getLoggedInUserId();
        return cartService.findByUserId(userId);
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    @GetMapping("/all")
    @Operation(summary = "List all shopping carts (paged)",
            description = "Accessible by any role")
    public Page<ShoppingCartDto> getAllCarts(Pageable pageable) {
        return cartService.findAllUserCarts(pageable);
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    @PostMapping
    @Operation(summary = "Add item to cart",
            description = "Add book to the shopping cart")
    public ShoppingCartDto addItem(@Valid @RequestBody CreateCartItemDto itemDto) {
        Long userId = SecurityUtil.getLoggedInUserId();
        return cartService.addItemToCart(itemDto, userId);
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    @PutMapping("cart-items/{cartItemId}")
    @Operation(summary = "Add item to cart",
            description = "Update quantity of a book in the shopping cart")
    public ShoppingCartDto updateItem(@PathVariable Long cartItemId,
                                      @Valid @RequestBody UpdateCartItemDto updateCartItemDto) {
        return cartService.updateQuantity(cartItemId, updateCartItemDto);
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    @DeleteMapping("cart-items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete cart item by id",
            description = "Remove an item from the shopping cart")
    public void deleteItemById(@PathVariable Long cartItemId) {
        cartService.deleteById(cartItemId);
    }
}
