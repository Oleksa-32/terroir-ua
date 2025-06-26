package com.example.backend.repository;

import com.example.backend.model.ShoppingCart;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @EntityGraph(attributePaths = {"cartItems", "cartItems.wine"})
    Optional<ShoppingCart> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"cartItems", "cartItems.wine"})
    Optional<ShoppingCart> findByCartItemsId(Long shoppingCartItemId);

    Page<ShoppingCart> findByUser_Id(Long userId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.shoppingCart.id = :cartId")
    void clearShoppingCart(Long cartId);
}
