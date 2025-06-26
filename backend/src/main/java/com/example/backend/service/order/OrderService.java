package com.example.backend.service.order;

import com.example.backend.dto.order.CreateOrderDto;
import com.example.backend.dto.order.OrderDto;
import com.example.backend.dto.orderitem.OrderItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto create(CreateOrderDto dto);

    Page<OrderDto> getOrders(Pageable pageable);

    Page<OrderItemDto> getOrderItems(Long orderId, Pageable pageable);

    OrderItemDto getOrderItem(Long orderId, Long itemId);
}
