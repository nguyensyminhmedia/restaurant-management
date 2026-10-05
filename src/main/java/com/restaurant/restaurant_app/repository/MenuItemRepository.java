package com.restaurant.restaurant_app.repository;

import com.restaurant.restaurant_app.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
}
