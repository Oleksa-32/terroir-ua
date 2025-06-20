package com.example.backend.mapper;

import com.example.backend.config.MapperConfig;
import com.example.backend.dto.shoppingcart.ShoppingCartDto;
import com.example.backend.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = {CartItemMapper.class})
public interface ShoppingCartMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "deliveryPrice", source = "deliveryPrice")
    @Mapping(target = "totalPrice", source = "totalPrice")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);

    @Mapping(target = "cartItems", source = "cartItems",
            qualifiedByName = "cartItemSetToModel")
    @Mapping(target = "user.id", source = "userId")
    ShoppingCart toModel(ShoppingCartDto shoppingCartDto);
}
