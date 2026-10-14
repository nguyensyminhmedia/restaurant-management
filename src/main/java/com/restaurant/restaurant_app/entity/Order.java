package com.restaurant.restaurant_app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    private RestaurantTable table;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(length = 120)
    private String customerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public long getTotal() {
        return items.stream().mapToLong(OrderItem::getSubtotal).sum();
    }

    public int getGuestCount() {
        return table == null ? 0 : Math.max(1, table.getSeats());
    }

    /** Ma don hien thi kieu #HD-1048, suy ra tu id de khong phai luu them cot. */
    @Transient
    public String getCode() {
        return id == null ? "#HD-????" : "#HD-" + (1000 + id);
    }

    @Transient
    public String getCustomerDisplay() {
        return (customerName == null || customerName.isBlank()) ? "Khách lẻ" : customerName;
    }
}
