package com.restaurant.restaurant_app.service;

import com.restaurant.restaurant_app.dto.CartLine;
import com.restaurant.restaurant_app.entity.*;
import com.restaurant.restaurant_app.repository.MenuItemRepository;
import com.restaurant.restaurant_app.repository.OrderRepository;
import com.restaurant.restaurant_app.repository.RestaurantTableRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private static final String CART_KEY = "cart";

    private final MenuItemRepository menuItemRepository;
    private final OrderRepository orderRepository;
    private final RestaurantTableRepository tableRepository;

    @SuppressWarnings("unchecked")
    public List<CartLine> get(HttpSession session) {
        return (List<CartLine>) session.getAttribute(CART_KEY);
    }

    public List<CartLine> add(HttpSession session, Long menuItemId, int quantity) {
        MenuItem item = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy món"));
        if (!item.isAvailable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Món này đã hết");
        }

        List<CartLine> lines = lines(session);
        lines.stream()
                .filter(l -> l.getMenuItemId().equals(menuItemId))
                .findFirst()
                .ifPresentOrElse(
                        l -> l.setQuantity(l.getQuantity() + quantity),
                        () -> lines.add(new CartLine(item.getId(), item.getName(), item.getPrice(), quantity))
                );
        return lines;
    }

    public List<CartLine> changeQuantity(HttpSession session, Long menuItemId, int delta) {
        List<CartLine> lines = lines(session);
        lines.stream()
                .filter(l -> l.getMenuItemId().equals(menuItemId))
                .findFirst()
                .ifPresent(l -> l.setQuantity(l.getQuantity() + delta));
        lines.removeIf(l -> l.getQuantity() <= 0);
        return lines;
    }

    public List<CartLine> remove(HttpSession session, Long menuItemId) {
        List<CartLine> lines = lines(session);
        lines.removeIf(l -> l.getMenuItemId().equals(menuItemId));
        return lines;
    }

    public void clear(HttpSession session) {
        session.removeAttribute(CART_KEY);
    }

    public long total(List<CartLine> lines) {
        return lines.stream().mapToLong(CartLine::getSubtotal).sum();
    }

    /**
     * Một bàn chỉ có tối đa một đơn đang mở: khách gọi thêm sẽ nối vào đơn cũ,
     * nhờ vậy thu ngân chỉ cần thanh toán một lần cho cả buổi.
     */
    @Transactional
    public Order submit(HttpSession session, Long tableId, String note) {
        List<CartLine> lines = lines(session);
        if (lines.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giỏ hàng đang trống");
        }

        RestaurantTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bàn"));

        Order order = orderRepository.findFirstByTableAndStatusNotOrderByCreatedAtAsc(table, OrderStatus.PAID)
                .orElseGet(Order::new);
        order.setTable(table);
        if (note != null && !note.isBlank()) {
            order.setNote(note.trim());
        }

        for (CartLine line : lines) {
            MenuItem item = menuItemRepository.findById(line.getMenuItemId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Món không còn tồn tại"));
            OrderItem orderItem = new OrderItem(order, item, line.getQuantity());
            orderItem.setPrice(item.getPrice());
            order.getItems().add(orderItem);
        }

        if (table.getStatus() == TableStatus.EMPTY) {
            table.setStatus(TableStatus.OCCUPIED);
            tableRepository.save(table);
        }

        Order saved = orderRepository.save(order);
        clear(session);
        return saved;
    }

    private List<CartLine> lines(HttpSession session) {
        Object existing = session.getAttribute(CART_KEY);
        if (existing instanceof List<?> list) {
            @SuppressWarnings("unchecked")
            List<CartLine> lines = (List<CartLine>) list;
            return lines;
        }
        List<CartLine> lines = new ArrayList<>();
        session.setAttribute(CART_KEY, lines);
        return lines;
    }
}
