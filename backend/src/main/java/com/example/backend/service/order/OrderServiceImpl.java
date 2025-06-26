package com.example.backend.service.order;

import com.example.backend.dto.order.CreateOrderDto;
import com.example.backend.dto.order.OrderDto;
import com.example.backend.dto.orderitem.OrderItemDto;
import com.example.backend.mapper.OrderItemMapper;
import com.example.backend.mapper.OrderMapper;
import com.example.backend.model.Order;
import com.example.backend.model.OrderItem;
import com.example.backend.model.ShoppingCart;
import com.example.backend.repository.CartItemRepository;
import com.example.backend.repository.OrderItemRepository;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.ShoppingCartRepository;
import com.example.backend.security.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    private final ShoppingCartRepository cartRepo;
    private final CartItemRepository cartItemRepo;
    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    public OrderDto create(CreateOrderDto dto) {
        Long userId = SecurityUtil.getLoggedInUserId();
        ShoppingCart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        if (cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        Order order = orderMapper.toCreateReadyOrderFromCart(cart, dto);
        order.setUser(cart.getUser());
        Set<OrderItem> items = cart.getCartItems().stream()
                .map(ci -> orderItemMapper.toOrderItemFromCartItem(ci, order))
                .collect(Collectors.toSet());
        order.setOrderItems(items);

        orderRepo.save(order);
        cartItemRepo.clearShoppingCart(cart.getId());
        return orderMapper.toDto(order);
    }

    @Override
    public Page<OrderDto> getOrders(Pageable pageable) {
        Long userId = SecurityUtil.getLoggedInUserId();
        return orderRepo.findAllByUserId(userId).stream()
                .map(orderMapper::toDto)
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> new PageImpl<>(list, pageable, list.size())
                ));
    }

    @Override
    public Page<OrderItemDto> getOrderItems(Long orderId, Pageable pageable) {
        Long userId = SecurityUtil.getLoggedInUserId();
        return orderItemRepo.findAllByOrderIdAndOrderUserId(orderId, userId, pageable)
                .map(orderItemMapper::toDto);
    }

    @Override
    public OrderItemDto getOrderItem(Long orderId, Long itemId) {
        Long userId = SecurityUtil.getLoggedInUserId();
        OrderItem oi = orderItemRepo
                .findByIdAndOrderIdAndOrderUserId(itemId, orderId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        return orderItemMapper.toDto(oi);
    }
}
