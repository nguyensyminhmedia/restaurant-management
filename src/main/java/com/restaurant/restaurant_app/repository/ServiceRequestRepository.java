package com.restaurant.restaurant_app.repository;

import com.restaurant.restaurant_app.entity.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    List<ServiceRequest> findByResolvedFalseOrderByCreatedAtDesc();

    long countByResolvedFalse();
}
