package com.restaurant.restaurant_app.repository;

import com.restaurant.restaurant_app.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    @Query("SELECT m FROM MenuItem m " +
            "WHERE m.available = true " +
            "AND (:categoryId IS NULL OR m.category.id = :categoryId) " +
            "AND LOWER(m.name) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "ORDER BY m.id")
    List<MenuItem> search(@Param("categoryId") Long categoryId, @Param("q") String q);
}
