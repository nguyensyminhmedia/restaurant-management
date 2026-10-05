package com.restaurant.restaurant_app.repository;

import com.restaurant.restaurant_app.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
}
