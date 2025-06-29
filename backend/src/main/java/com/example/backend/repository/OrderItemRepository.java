package com.example.backend.repository;

import com.example.backend.model.OrderItem;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @EntityGraph(attributePaths = "order")
    Optional<OrderItem> findByIdAndOrderIdAndOrderUserId(Long id, Long orderId, Long userId);

    @EntityGraph(attributePaths = "order")
    Page<OrderItem> findAllByOrderIdAndOrderUserId(Long orderId, Long userId, Pageable pageable);
}
