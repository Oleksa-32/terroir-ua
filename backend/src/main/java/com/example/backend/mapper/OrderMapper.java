package com.example.backend.mapper;

import com.example.backend.config.MapperConfig;
import com.example.backend.dto.order.CreateOrderDto;
import com.example.backend.dto.order.OrderDto;
import com.example.backend.model.CartItem;
import com.example.backend.model.Order;
import com.example.backend.model.ShoppingCart;
import java.math.BigDecimal;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface OrderMapper {

    OrderDto toDto(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    Order toModel(CreateOrderDto createOrderDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(source = "dto.firstName", target = "firstName")
    @Mapping(source = "dto.lastName", target = "lastName")
    @Mapping(source = "dto.shippingAddress", target = "shippingAddress")
    @Mapping(source = "dto.zipCode", target = "zipCode")
    @Mapping(source = "dto.phoneNumber", target = "phoneNumber")
    @Mapping(source = "dto.details", target = "details")
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "totalPrice", source = "cart.cartItems", qualifiedByName = "calcTotal")
    Order toCreateReadyOrderFromCart(ShoppingCart cart, CreateOrderDto dto);

    @Named("calcTotal")
    default BigDecimal calcTotal(Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(ci -> ci.getWine().getPrice()
                        .multiply(BigDecimal.valueOf(ci.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
