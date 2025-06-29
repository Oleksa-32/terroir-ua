package com.example.backend.controller;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.backend.dto.shoppingcart.ShoppingCartDto;
import com.example.backend.utils.TestDataUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(
        scripts = {
                "classpath:database/delete-all.sql",
                "classpath:database/wine/init-wines.sql",
                "classpath:database/cart/add-cart.sql",
                "classpath:database/cart/add-cart-items.sql",
                "classpath:database/reset‐identities.sql"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class ShoppingCartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "1", roles = {"CUSTOMER"})
    void getCart_ShouldReturnShoppingCartDto() throws Exception {
        ShoppingCartDto expected = TestDataUtil.createShoppingCartDto();

        String json = mockMvc.perform(get("/cart")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ShoppingCartDto actual = objectMapper.readValue(json, ShoppingCartDto.class);

        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getAmount(), actual.getAmount());
        assertTrue(
                expected.getDeliveryPrice().compareTo(actual.getDeliveryPrice()) == 0,
                () -> "Expected deliveryPrice " + expected.getDeliveryPrice()
                        + " but was " + actual.getDeliveryPrice()
        );
        assertEquals(expected.getTotalPrice(), actual.getTotalPrice());

        List<Long> expIds = expected.getCartItems().stream()
                .map(ci -> ci.getWineId())
                .sorted()
                .collect(toList());
        List<Long> actIds = actual.getCartItems().stream()
                .map(ci -> ci.getWineId())
                .sorted()
                .collect(toList());
        assertEquals(expIds, actIds);

        expected.getCartItems().forEach(expCi -> {
            actual.getCartItems().stream()
                    .filter(actCi -> actCi.getWineId().equals(expCi.getWineId()))
                    .findFirst()
                    .ifPresentOrElse(
                            actCi -> assertEquals(expCi.getQuantity(), actCi.getQuantity()),
                            () -> fail("Missing wine " + expCi.getWineId()));
        });
    }

    @Test
    @WithMockUser(username = "1", roles = {"CUSTOMER"})
    void addItem_ShouldReturnUpdatedCart() throws Exception {
        String payload = objectMapper.writeValueAsString(
                TestDataUtil.createCreateCartItemDto(3L, 1)
        );

        String json = mockMvc.perform(post("/cart")
                        .with(csrf())
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ShoppingCartDto actual = objectMapper.readValue(json, ShoppingCartDto.class);

        assertTrue(
                actual.getCartItems().stream()
                        .anyMatch(ci -> ci.getWineId().equals(3L) && ci.getQuantity() == 1),
                "New wine 3 should have been added with quantity 1"
        );
    }

    @Test
    @WithMockUser(username = "1", roles = {"CUSTOMER"})
    void updateQuantity_ShouldRecalculateTotals() throws Exception {
        String payload = objectMapper.writeValueAsString(
                TestDataUtil.createUpdateCartItemDto(5)
        );

        String json = mockMvc.perform(put("/cart/cart-items/1")
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ShoppingCartDto actual = objectMapper.readValue(json, ShoppingCartDto.class);

        assertEquals(
                5,
                actual.getCartItems().stream()
                        .filter(ci -> ci.getId().equals(1L))
                        .findFirst()
                        .get()
                        .getQuantity()
        );
    }

    @Sql(
            scripts = "classpath:database/cart/add-new-item.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/cart/remove-new-item.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @Test
    @WithMockUser(username = "1", roles = {"CUSTOMER"})
    void deleteItem_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/cart/cart-items/3"))
                .andExpect(status().isNoContent());
    }
}
