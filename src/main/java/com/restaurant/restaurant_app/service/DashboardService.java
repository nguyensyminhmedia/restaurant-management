package com.restaurant.restaurant_app.service;

import com.restaurant.restaurant_app.entity.Order;
import com.restaurant.restaurant_app.entity.OrderStatus;
import com.restaurant.restaurant_app.entity.RestaurantTable;
import com.restaurant.restaurant_app.entity.ServiceRequest;
import com.restaurant.restaurant_app.entity.TableStatus;
import com.restaurant.restaurant_app.repository.MenuItemRepository;
import com.restaurant.restaurant_app.repository.OrderRepository;
import com.restaurant.restaurant_app.repository.RestaurantTableRepository;
import com.restaurant.restaurant_app.repository.ServiceRequestRepository;
import com.restaurant.restaurant_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final List<OrderStatus> OPEN_STATUSES = List.of(
            OrderStatus.PENDING, OrderStatus.COOKING, OrderStatus.READY, OrderStatus.SERVED);

    private final OrderRepository orderRepository;
    private final RestaurantTableRepository tableRepository;
    private final MenuItemRepository menuItemRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final UserRepository userRepository;

    public long revenueToday() {
        return orderRepository.findTodayByStatus(startOfToday(), OrderStatus.PAID)
                .stream().mapToLong(Order::getTotal).sum();
    }

    public long openOrderCount() {
        return orderRepository.findByStatusInOrderByCreatedAtAsc(OPEN_STATUSES).size();
    }

    public long waitingKitchenCount() {
        List<OrderStatus> waiting = List.of(OrderStatus.PENDING, OrderStatus.COOKING);
        return orderRepository.findByStatusInOrderByCreatedAtAsc(waiting).size();
    }

    public List<Order> recentOrders(int limit) {
        return orderRepository.findToday(startOfToday(), PageRequest.of(0, limit));
    }

    public List<RestaurantTable> tables() {
        return tableRepository.findAll();
    }

    public long tablesInUse() {
        return tableRepository.findAll().stream()
                .filter(t -> t.getStatus() == TableStatus.OCCUPIED)
                .count();
    }

    public long tableCount() {
        return tableRepository.count();
    }

    public int occupancyPercent() {
        long total = tableCount();
        if (total == 0) {
            return 0;
        }
        return (int) Math.round(tablesInUse() * 100.0 / total);
    }

    public long menuItemCount() {
        return menuItemRepository.count();
    }

    public long staffCount() {
        return userRepository.count();
    }

    public List<ServiceRequest> openRequests() {
        return serviceRequestRepository.findByResolvedFalseOrderByCreatedAtDesc();
    }

    public long openRequestCount() {
        return serviceRequestRepository.countByResolvedFalse();
    }

    private LocalDateTime startOfToday() {
        return LocalDate.now().atStartOfDay();
    }
}
