package com.example.backend.mapper;

import com.example.backend.config.MapperConfig;
import com.example.backend.dto.cartitem.CreateCartItemDto;
import com.example.backend.dto.cartitem.UpdateCartItemDto;
import com.example.backend.dto.shoppingcart.CartItemDto;
import com.example.backend.model.CartItem;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class, uses = {WineMapper.class})
public interface CartItemMapper {
    @Mapping(source = "wine.id", target = "wineId")
    CartItemDto toDto(CartItem cartItem);

    @Mapping(target = "wine", source = "wineId", qualifiedByName = "wineFromId")
    CartItem createToModel(CreateCartItemDto createCartItemDto);

    @Mapping(target = "wine", source = "wineId", qualifiedByName = "wineFromId")
    CartItem dtoToModel(CartItemDto cartItemDto);

    @Mapping(target = "wine", source = "wineId", qualifiedByName = "wineFromId")
    CartItem toModel(CreateCartItemDto cartItemDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "quantity", target = "quantity")
    void updateFromDto(UpdateCartItemDto updateCartItemDto, @MappingTarget CartItem cartItem);

    @Named("cartItemSetToModel")
    default Set<CartItem> cartItemSetToModel(Set<CartItemDto> cartItemDtoSet) {
        if (cartItemDtoSet == null) {
            return new HashSet<>();
        }
        return cartItemDtoSet.stream()
                .map(this::dtoToModel)
                .collect(Collectors.toSet());
    }

}
