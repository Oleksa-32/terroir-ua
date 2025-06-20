package com.example.backend.repository;

import com.example.backend.model.CartItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @EntityGraph(attributePaths = {"wine", "shoppingCart", "shoppingCart.user"})
    Optional<CartItem> findByWine_IdAndShoppingCart_Id(Long wineId, Long userId);

    @EntityGraph(attributePaths = {"wine", "shoppingCart"})
    Optional<CartItem> findById(Long id);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.shoppingCart.id = :cartId")
    void clearShoppingCart(Long cartId);
}
