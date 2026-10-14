package com.restaurant.restaurant_app.repository;

import com.restaurant.restaurant_app.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByPaidAtBetween(LocalDateTime from, LocalDateTime to);
}
