package com.restaurant.restaurant_app.repository;

import com.restaurant.restaurant_app.entity.Order;
import com.restaurant.restaurant_app.entity.OrderStatus;
import com.restaurant.restaurant_app.entity.RestaurantTable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatusInOrderByCreatedAtAsc(List<OrderStatus> statuses);

    List<Order> findByTableAndStatusNotOrderByCreatedAtAsc(RestaurantTable table, OrderStatus status);

    Optional<Order> findFirstByTableAndStatusNotOrderByCreatedAtAsc(RestaurantTable table, OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.createdAt >= :from ORDER BY o.createdAt DESC")
    List<Order> findToday(@Param("from") LocalDateTime from, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.createdAt >= :from AND o.status = :status")
    List<Order> findTodayByStatus(@Param("from") LocalDateTime from,
                                  @Param("status") OrderStatus status);
}
