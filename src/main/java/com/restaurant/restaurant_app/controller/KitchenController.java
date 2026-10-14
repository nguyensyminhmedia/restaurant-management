package com.restaurant.restaurant_app.controller;

import com.restaurant.restaurant_app.entity.Order;
import com.restaurant.restaurant_app.entity.OrderItem;
import com.restaurant.restaurant_app.entity.OrderStatus;
import com.restaurant.restaurant_app.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/kitchen")
@RequiredArgsConstructor
public class KitchenController {

    private static final List<OrderStatus> ACTIVE = List.of(
            OrderStatus.PENDING, OrderStatus.COOKING, OrderStatus.READY, OrderStatus.SERVED);

    private final OrderRepository orderRepository;

    @GetMapping
    public String board(Model model) {
        model.addAttribute("orders", orderRepository.findByStatusInOrderByCreatedAtAsc(ACTIVE));
        return "kitchen/board";
    }

    @PostMapping("/orders/{orderId}/items/{itemId}/toggle")
    public String toggleItem(@PathVariable Long orderId, @PathVariable Long itemId,
                             RedirectAttributes redirect) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .ifPresent(i -> i.setDone(!i.isDone()));

        boolean allDone = order.getItems().stream().allMatch(OrderItem::isDone);
        if (allDone && order.getStatus() == OrderStatus.COOKING) {
            order.setStatus(OrderStatus.READY);
            redirect.addFlashAttribute("message", "Bàn " + order.getTable().getName() + " đã nấu xong.");
        }
        orderRepository.save(order);
        return "redirect:/kitchen";
    }

    @PostMapping("/orders/{orderId}/status")
    public String setStatus(@PathVariable Long orderId,
                            @RequestParam OrderStatus status,
                            RedirectAttributes redirect) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus(status);
        if (status == OrderStatus.COOKING) {
            order.getItems().forEach(i -> i.setDone(false));
        }
        orderRepository.save(order);
        return "redirect:/kitchen";
    }
}
