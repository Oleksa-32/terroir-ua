package com.example.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.backend.dto.cartitem.CreateCartItemDto;
import com.example.backend.dto.cartitem.UpdateCartItemDto;
import com.example.backend.dto.shoppingcart.ShoppingCartDto;
import com.example.backend.mapper.CartItemMapper;
import com.example.backend.mapper.ShoppingCartMapper;
import com.example.backend.model.CartItem;
import com.example.backend.model.ShoppingCart;
import com.example.backend.model.Wine;
import com.example.backend.repository.CartItemRepository;
import com.example.backend.repository.ShoppingCartRepository;
import com.example.backend.repository.WineRepository;
import com.example.backend.service.shoppingcart.ShoppingCartServiceImpl;
import com.example.backend.utils.TestDataUtil;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private ShoppingCartMapper shoppingCartMapper;

    @Mock
    private WineRepository wineRepository;

    @Mock
    private CartItemMapper cartItemMapper;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private ShoppingCartServiceImpl service;

    private final Long userId = 42L;
    private final Long wineId = 99L;

    private ShoppingCart cart;
    private Wine wine;

    @BeforeEach
    void setUp() {
        cart = TestDataUtil.shoppingCart(userId);
        wine = TestDataUtil.wine(wineId, BigDecimal.valueOf(12.34));
    }

    @Test
    @DisplayName("findByUserId returns mapped DTO")
    void findByUserId_returnsDto() {
        ShoppingCartDto dto = TestDataUtil.shoppingCartDto(userId);
        when(shoppingCartRepository.findByUserId(userId))
                .thenReturn(Optional.of(cart));
        when(shoppingCartMapper.toDto(cart))
                .thenReturn(dto);

        ShoppingCartDto actual = service.findByUserId(userId);

        assertThat(actual).isEqualTo(dto);
        verify(shoppingCartRepository).findByUserId(userId);
        verify(shoppingCartMapper).toDto(cart);
    }

    @Test
    @DisplayName("findByUserId missing cart throws")
    void findByUserId_missing_throws() {
        when(shoppingCartRepository.findByUserId(userId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByUserId(userId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("can't find cart by user id " + userId);
    }

    @Test
    @DisplayName("addItemToCart: new item → returns updated DTO")
    void addItemToCart_new() {
        CreateCartItemDto createDto = TestDataUtil.createCartItemDto(wineId, 3);
        CartItem newItem = TestDataUtil.cartItem(null, cart, wine, 3);
        ShoppingCartDto expectedDto = TestDataUtil.shoppingCartDto(userId);

        when(wineRepository.findById(wineId))
                .thenReturn(Optional.of(wine));
        when(shoppingCartRepository.findByUserId(userId))
                .thenReturn(Optional.of(cart));
        when(cartItemRepository.findByWine_IdAndShoppingCart_Id(wineId, userId))
                .thenReturn(Optional.empty());
        when(cartItemMapper.createToModel(createDto))
                .thenReturn(newItem);
        when(shoppingCartMapper.toDto(cart))
                .thenReturn(expectedDto);

        ShoppingCartDto actual = service.addItemToCart(createDto, userId);

        assertThat(actual).isEqualTo(expectedDto);
        verify(cartItemMapper).createToModel(createDto);
        verify(shoppingCartRepository).save(cart);
        verify(shoppingCartMapper).toDto(cart);
    }

    @Test
    @DisplayName("addItemToCart: existing item increments quantity")
    void addItemToCart_existing() {
        CreateCartItemDto createDto = TestDataUtil.createCartItemDto(wineId, 2);
        CartItem existing = TestDataUtil.cartItem(7L, cart, wine, 5);

        when(wineRepository.findById(wineId))
                .thenReturn(Optional.of(wine));
        when(shoppingCartRepository.findByUserId(userId))
                .thenReturn(Optional.of(cart));
        when(cartItemRepository.findByWine_IdAndShoppingCart_Id(wineId, userId))
                .thenReturn(Optional.of(existing));
        when(shoppingCartMapper.toDto(cart))
                .thenReturn(TestDataUtil.shoppingCartDto(userId));

        ShoppingCartDto actual = service.addItemToCart(createDto, userId);

        assertThat(existing.getQuantity()).isEqualTo(7);
        verify(cartItemRepository).save(existing);
        verify(shoppingCartMapper).toDto(cart);
    }

    @Test
    @DisplayName("updateQuantity: changes qty and recalculates")
    void updateQuantity_valid() {
        long itemId = 55L;
        UpdateCartItemDto upd = TestDataUtil.updateCartItemDto(itemId, 4);
        CartItem toUpdate = TestDataUtil.cartItem(itemId, cart, wine, 1);
        cart.getCartItems().add(toUpdate);

        when(cartItemRepository.findById(itemId))
                .thenReturn(Optional.of(toUpdate));
        when(shoppingCartRepository.findByCartItemsId(itemId))
                .thenReturn(Optional.of(cart));
        when(shoppingCartMapper.toDto(cart))
                .thenReturn(TestDataUtil.shoppingCartDto(userId));

        doAnswer(invocation -> {
            UpdateCartItemDto dtoArg = invocation.getArgument(0);
            CartItem itemArg = invocation.getArgument(1);
            itemArg.setQuantity(dtoArg.getQuantity());
            return null;
        }).when(cartItemMapper).updateFromDto(eq(upd), eq(toUpdate));

        ShoppingCartDto actual = service.updateQuantity(itemId, upd);

        assertThat(toUpdate.getQuantity()).isEqualTo(4);
        verify(cartItemRepository).save(toUpdate);
        verify(shoppingCartRepository).save(cart);
        assertThat(actual.getUserId()).isEqualTo(userId);
    }
}
