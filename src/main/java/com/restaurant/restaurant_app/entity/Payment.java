package com.restaurant.restaurant_app.entity;

import com.restaurant.restaurant_app.entity.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PayMethod method = PayMethod.CASH;

    @Column(nullable = false)
    private long amount;

    @Column(nullable = false)
    private LocalDateTime paidAt = LocalDateTime.now();
}
