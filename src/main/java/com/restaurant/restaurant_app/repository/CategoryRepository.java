package com.restaurant.restaurant_app.repository;

import com.restaurant.restaurant_app.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
