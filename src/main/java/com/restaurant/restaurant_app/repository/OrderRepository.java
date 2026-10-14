package com.restaurant.restaurant_app.repository;

import com.restaurant.restaurant_app.entity.Order;
import com.restaurant.restaurant_app.entity.OrderStatus;
import com.restaurant.restaurant_app.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatusInOrderByCreatedAtAsc(List<OrderStatus> statuses);

    List<Order> findByTableAndStatusNotOrderByCreatedAtAsc(RestaurantTable table, OrderStatus status);

    Optional<Order> findFirstByTableAndStatusNotOrderByCreatedAtAsc(RestaurantTable table, OrderStatus status);
}
