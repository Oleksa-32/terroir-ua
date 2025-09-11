package com.example.backend.utils;

import com.example.backend.dto.cartitem.CartItemDto;
import com.example.backend.dto.cartitem.CreateCartItemDto;
import com.example.backend.dto.cartitem.UpdateCartItemDto;
import com.example.backend.dto.shoppingcart.ShoppingCartDto;
import com.example.backend.dto.user.UpdateUserProfileRequestDto;
import com.example.backend.dto.user.UpdateUserRoleRequestDto;
import com.example.backend.dto.user.UserRegistrationRequestDto;
import com.example.backend.dto.user.UserResponseDto;
import com.example.backend.dto.wine.CreateWineRequestDto;
import com.example.backend.dto.wine.UpdateWineRequestDto;
import com.example.backend.dto.wine.WineDto;
import com.example.backend.dto.wine.WineItemDto;
import com.example.backend.dto.wine.WineRecommendationDto;
import com.example.backend.model.CartItem;
import com.example.backend.model.Role;
import com.example.backend.model.ShoppingCart;
import com.example.backend.model.Types;
import com.example.backend.model.User;
import com.example.backend.model.Wine;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class TestDataUtil {

    private TestDataUtil() {
    }

    public static Wine createWine(Long id, String name, int year, Types type,
                                  BigDecimal price, String producer) {
        return new Wine()
                .setId(id)
                .setName(name)
                .setYear(year)
                .setType(type)
                .setPrice(price)
                .setProducer(producer)
                .setDescription("Description")
                .setOwnerDescription("Owner description")
                .setRate(BigDecimal.valueOf(4.5))
                .setAgingMethod("Oak")
                .setSweetness("Dry")
                .setRegion("Bordeaux")
                .setVariety("Cabernet Sauvignon")
                .setPercentage(BigDecimal.valueOf(13.5))
                .setDateAdded(LocalDateTime.now())
                .setVolume(750)
                .setImageUrl("/images/wine" + id + ".jpg");
    }

    public static WineDto createWineDto(Long id) {
        return new WineDto()
                .setId(id)
                .setName("Test Wine")
                .setYear(2020)
                .setType(Types.RED.getLabel())
                .setPrice(BigDecimal.valueOf(25.99))
                .setProducer("Test Producer")
                .setDescription("Description")
                .setOwnerDescription("Owner description")
                .setRate(BigDecimal.valueOf(4.5))
                .setAgingMethod("Oak")
                .setSweetness("Dry")
                .setRegion("Bordeaux")
                .setVariety("Cabernet Sauvignon")
                .setPercentage(BigDecimal.valueOf(13.5))
                .setDateAdded(LocalDateTime.now())
                .setVolume(750)
                .setImageUrl("/images/wine" + id + ".jpg");
    }

    public static WineItemDto createWineItemDto(Long id) {
        return new WineItemDto()
                .setName("Wine " + id)
                .setYear(2020)
                .setPrice(BigDecimal.valueOf(20.99));
    }

    public static WineRecommendationDto createWineRecommendationDto(Long id) {
        return (WineRecommendationDto) new WineRecommendationDto()
                .setImageUrl("/images/wine" + id + ".jpg")
                .setId(id)
                .setName("Wine " + id)
                .setYear(2020)
                .setPrice(BigDecimal.valueOf(20.99));
    }

    public static List<WineRecommendationDto> createWineRecommendationDtoList() {
        return List.of(
                createWineRecommendationDto(1L),
                createWineRecommendationDto(2L),
                createWineRecommendationDto(3L)
        );
    }

    public static CreateWineRequestDto createWineRequestDto() {
        return new CreateWineRequestDto()
                .setName("New Wine")
                .setYear(2020)
                .setType(Types.RED.getLabel())
                .setPrice(BigDecimal.valueOf(29.99))
                .setProducer("New Producer")
                .setDescription("Description")
                .setOwnerDescription("Owner description")
                .setRate(BigDecimal.valueOf(4.5))
                .setAgingMethod("Oak")
                .setSweetness("Dry")
                .setRegion("Bordeaux")
                .setVariety("Cabernet Sauvignon")
                .setPercentage(BigDecimal.valueOf(13.5))
                .setDateAdded(LocalDateTime.now())
                .setVolume(750);
    }

    public static UpdateWineRequestDto updateWineRequestDto() {
        return new UpdateWineRequestDto()
                .setName("Updated Wine")
                .setYear(2018)
                .setType(Types.WHITE.getLabel())
                .setPrice(BigDecimal.valueOf(35.99))
                .setProducer("Updated Producer");
    }

    public static List<Wine> createWineList() {
        return List.of(
                createWine(1L, "Wine One", 2020, Types.RED, BigDecimal.valueOf(25.99),
                        "Producer A"),
                createWine(2L, "Wine Two", 2018, Types.WHITE, BigDecimal.valueOf(19.99),
                        "Producer B"),
                createWine(3L, "Wine Three", 2015, Types.RED, BigDecimal.valueOf(45.99),
                        "Producer C")
        );
    }

    public static List<WineItemDto> createWineItemDtoList() {
        return List.of(
                createWineItemDto(1L),
                createWineItemDto(2L),
                createWineItemDto(3L)
        );
    }

    public static User user(Long id, String email, String name, String password, Role... roles) {
        User user = new User()
                .setId(id)
                .setEmail(email)
                .setName(name)
                .setPassword(password);
        user.getRoles().clear();
        for (Role r : roles) {
            user.getRoles().add(r);
        }
        return user;
    }

    public static User user(Long id) {
        return new User()
                .setId(id)
                .setEmail("user" + id + "@example.com")
                .setName("User " + id)
                .setPassword("password");
    }

    public static User user() {
        return new User()
                .setId(1L)
                .setEmail("test@example.com")
                .setName("Test User")
                .setPassword("password");
    }

    public static Role role(Long id, Role.Roles name) {
        return new Role().setId(id).setName(name);
    }

    public static UserRegistrationRequestDto createUserRegistrationRequestDto() {
        return new UserRegistrationRequestDto()
                .setEmail("test@domain.com")
                .setPassword("password123")
                .setRepeatPassword("password123")
                .setName("Test User");
    }

    public static UserResponseDto mapToUserResponseDto(User user) {
        return new UserResponseDto()
                .setId(user.getId())
                .setEmail(user.getEmail())
                .setName(user.getName());
    }

    public static UpdateUserRoleRequestDto createUpdateUserRoleRequestDto() {
        return new UpdateUserRoleRequestDto().setRole(Role.Roles.ROLE_MANAGER);
    }

    public static UpdateUserProfileRequestDto createUpdateUserProfileRequestDto() {
        return new UpdateUserProfileRequestDto()
                .setName("New Name");
    }

    public static void applyProfileUpdate(UpdateUserProfileRequestDto dto, User user) {
        user.setName(dto.getName());
    }

    public static ShoppingCart shoppingCart(Long userId) {
        ShoppingCart c = new ShoppingCart();
        c.setId(userId);
        c.setUser(user(userId));
        c.getCartItems().clear();
        c.setAmount(BigDecimal.ZERO);
        c.setDeliveryPrice(BigDecimal.ZERO);
        c.setTotalPrice(BigDecimal.ZERO);
        return c;
    }

    public static ShoppingCartDto shoppingCartDto(Long userId) {
        ShoppingCartDto d = new ShoppingCartDto();
        d.setUserId(userId);
        d.getCartItems().clear();
        d.setAmount(BigDecimal.ZERO);
        d.setDeliveryPrice(BigDecimal.ZERO);
        d.setTotalPrice(BigDecimal.ZERO);
        return d;
    }

    public static Wine wine(Long id, BigDecimal price) {
        return new Wine()
                .setId(id)
                .setPrice(price)
                .setName("Test Wine")
                .setYear(2020)
                .setType(Types.RED)
                .setProducer("TestCo")
                .setDescription("desc")
                .setOwnerDescription("owner")
                .setRate(BigDecimal.valueOf(4))
                .setAgingMethod("Oak")
                .setSweetness("Dry")
                .setRegion("Bordeaux")
                .setVariety("Cab")
                .setPercentage(BigDecimal.valueOf(13.5))
                .setDateAdded(LocalDateTime.now())
                .setVolume(750)
                .setImageUrl("/img");
    }

    public static CartItem cartItem(Long id, ShoppingCart cart, Wine wine, int qty) {
        CartItem ci = new CartItem();
        ci.setId(id);
        ci.setShoppingCart(cart);
        ci.setWine(wine);
        ci.setQuantity(qty);
        return ci;
    }

    public static CreateCartItemDto createCartItemDto(Long wineId, int qty) {
        CreateCartItemDto dto = new CreateCartItemDto();
        dto.setWineId(wineId);
        dto.setQuantity(qty);
        return dto;
    }

    public static CartItemDto createCartItemDto(Long id, Long wineId, int quantity) {
        return new CartItemDto()
                .setId(id)
                .setWineId(wineId)
                .setQuantity(quantity);
    }

    public static CreateCartItemDto createCreateCartItemDto(Long wineId, int quantity) {
        return new CreateCartItemDto()
                .setWineId(wineId)
                .setQuantity(quantity);
    }

    public static UpdateCartItemDto updateCartItemDto(long id, int newQty) {
        UpdateCartItemDto dto = new UpdateCartItemDto();
        dto.setQuantity(newQty);
        return dto;
    }

    public static UpdateCartItemDto createUpdateCartItemDto(int quantity) {
        return new UpdateCartItemDto()
                .setQuantity(quantity);
    }

    public static ShoppingCartDto createShoppingCartDto() {
        CartItemDto item1 = createCartItemDto(1L, 1L, 2);
        CartItemDto item2 = createCartItemDto(2L, 2L, 3);

        BigDecimal amount = BigDecimal.valueOf(25.99).multiply(BigDecimal.valueOf(2))
                .add(BigDecimal.valueOf(19.99).multiply(BigDecimal.valueOf(3)));

        BigDecimal delivery = BigDecimal.valueOf(10);
        BigDecimal total = amount.add(delivery);

        return new ShoppingCartDto()
                .setUserId(1L)
                .setCartItems(Set.of(item1, item2))
                .setAmount(amount)
                .setDeliveryPrice(delivery)
                .setTotalPrice(total);
    }
}
