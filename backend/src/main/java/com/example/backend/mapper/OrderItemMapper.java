package com.example.backend.mapper;

import com.example.backend.config.MapperConfig;
import com.example.backend.dto.orderitem.OrderItemDto;
import com.example.backend.model.CartItem;
import com.example.backend.model.Order;
import com.example.backend.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = {WineMapper.class})
public interface OrderItemMapper {
    @Mapping(source = "wine.id", target = "wineId")
    @Mapping(source = "pricePerUnit", target = "pricePerUnit")
    OrderItemDto toDto(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "cartItem.wine.id", target = "wine.id")
    @Mapping(source = "cartItem.quantity", target = "quantity")
    @Mapping(source = "cartItem.wine.price", target = "pricePerUnit")
    @Mapping(source = "order", target = "order")
    OrderItem toOrderItemFromCartItem(CartItem cartItem, Order order);
}
