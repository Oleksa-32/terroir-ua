package com.example.backend.service.shoppingcart;

import com.example.backend.dto.cartitem.UpdateCartItemDto;
import com.example.backend.dto.shoppingcart.CreateCartItemDto;
import com.example.backend.dto.shoppingcart.ShoppingCartDto;
import com.example.backend.mapper.CartItemMapper;
import com.example.backend.mapper.ShoppingCartMapper;
import com.example.backend.model.CartItem;
import com.example.backend.model.ShoppingCart;
import com.example.backend.model.User;
import com.example.backend.model.Wine;
import com.example.backend.repository.CartItemRepository;
import com.example.backend.repository.ShoppingCartRepository;
import com.example.backend.repository.WineRepository;
import com.example.backend.security.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final WineRepository wineRepository;
    private final CartItemMapper cartItemMapper;
    private final CartItemRepository cartItemRepository;

    private void recalculateCartTotals(ShoppingCart shoppingCart) {
        BigDecimal amount = shoppingCart.getCartItems().stream()
                .map(item -> item.getWine().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal delivery = amount.compareTo(BigDecimal.valueOf(100)) >= 0
                ? BigDecimal.valueOf(10)
                : BigDecimal.valueOf(20);

        shoppingCart.setAmount(amount);
        shoppingCart.setDeliveryPrice(delivery);
        shoppingCart.setTotalPrice(amount.add(shoppingCart.getDeliveryPrice()));
    }

    @Override
    public ShoppingCartDto findByUserId(Long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId).orElseThrow(
                () -> new EntityNotFoundException("can't find cart by user id " + userId)
        );
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public Page<ShoppingCartDto> findAllUserCarts(Pageable pageable) {
        Long me = SecurityUtil.getLoggedInUserId();
        return shoppingCartRepository
                .findByUser_Id(me, pageable)
                .map(shoppingCartMapper::toDto);
    }

    @Override
    public ShoppingCartDto addItemToCart(CreateCartItemDto createCartItemDto, Long userId) {
        Wine wine = wineRepository.findById(createCartItemDto.getWineId())
                .orElseThrow(() -> new EntityNotFoundException(" can't find wine by id "
                        + createCartItemDto.getWineId()));
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("can't find cart with userid: "
                        + userId));

        Long wineId = createCartItemDto.getWineId();
        Optional<CartItem> existingCartItem = cartItemRepository
                .findByWine_IdAndShoppingCart_Id(wineId, userId);
        if (existingCartItem.isEmpty()) {
            CartItem cartItem = cartItemMapper.createToModel(createCartItemDto);
            cartItem.setWine(wine);
            cartItem.setShoppingCart(shoppingCart);
            shoppingCart.getCartItems().add(cartItem);
            recalculateCartTotals(shoppingCart);
            shoppingCartRepository.save(shoppingCart);
        } else {
            CartItem existingItem = existingCartItem.get();
            int newQuantity = existingItem.getQuantity() + createCartItemDto.getQuantity();
            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        }
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public void deleteById(Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found: "
                        + cartItemId));

        ShoppingCart cart = item.getShoppingCart();
        cart.getCartItems().remove(item);
        cartItemRepository.delete(item);

        BigDecimal newAmount = cart.getCartItems().stream()
                .map(ci -> ci.getWine().getPrice()
                        .multiply(BigDecimal.valueOf(ci.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setAmount(newAmount);

        BigDecimal delivery = newAmount.compareTo(BigDecimal.valueOf(100)) >= 0
                ? BigDecimal.valueOf(10)
                : BigDecimal.valueOf(20);
        cart.setDeliveryPrice(delivery);
        cart.setTotalPrice(newAmount.add(delivery));

        shoppingCartRepository.save(cart);
    }

    @Override
    public void createShoppingCartForUser(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setAmount(new BigDecimal(0));
        shoppingCart.setDeliveryPrice(new BigDecimal(0));
        shoppingCart.setTotalPrice(new BigDecimal(0));
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCartDto updateQuantity(Long cartItemId, UpdateCartItemDto updateCartItemDto) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find cart item by id "
                        + cartItemId));
        cartItemMapper.updateFromDto(updateCartItemDto, cartItem);
        cartItemRepository.save(cartItem);

        ShoppingCart shoppingCart = shoppingCartRepository
                .findByCartItemsId(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cart of cart item with id " + cartItemId + " wasn't found"));
        recalculateCartTotals(shoppingCart);
        shoppingCartRepository.save(shoppingCart);

        return shoppingCartMapper.toDto(shoppingCart);
    }
}
