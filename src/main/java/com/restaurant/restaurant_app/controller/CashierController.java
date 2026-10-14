package com.restaurant.restaurant_app.controller;

import com.restaurant.restaurant_app.entity.Order;
import com.restaurant.restaurant_app.entity.OrderStatus;
import com.restaurant.restaurant_app.entity.RestaurantTable;
import com.restaurant.restaurant_app.entity.TableStatus;
import com.restaurant.restaurant_app.repository.OrderRepository;
import com.restaurant.restaurant_app.repository.RestaurantTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/cashier")
@RequiredArgsConstructor
public class CashierController {

    private final OrderRepository orderRepository;
    private final RestaurantTableRepository tableRepository;

    @GetMapping
    public String list(Model model) {
        List<Order> open = orderRepository.findByStatusInOrderByCreatedAtAsc(
                List.of(OrderStatus.PENDING, OrderStatus.COOKING,
                        OrderStatus.READY, OrderStatus.SERVED));
        model.addAttribute("orders", open);
        model.addAttribute("tables", tableRepository.findAll(Sort.by("id")));
        return "cashier/list";
    }

    @GetMapping("/orders/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderRepository.findById(id).orElseThrow());
        return "cashier/detail";
    }

    @PostMapping("/orders/{id}/pay")
    public String pay(@PathVariable Long id, RedirectAttributes redirect) {
        Order order = orderRepository.findById(id).orElseThrow();
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        RestaurantTable table = order.getTable();
        boolean stillOpen = !orderRepository
                .findByTableAndStatusNotOrderByCreatedAtAsc(table, OrderStatus.PAID)
                .isEmpty();
        if (!stillOpen) {
            table.setStatus(TableStatus.EMPTY);
            tableRepository.save(table);
        }

        redirect.addFlashAttribute("message",
                "Đã thanh toán " + table.getName() + " — "
                        + String.format("%,d", order.getTotal()) + "đ");
        return "redirect:/cashier";
    }
}
