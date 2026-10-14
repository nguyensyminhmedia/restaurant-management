package com.restaurant.restaurant_app.controller;

import com.restaurant.restaurant_app.dto.CartLine;
import com.restaurant.restaurant_app.entity.Order;
import com.restaurant.restaurant_app.entity.OrderStatus;
import com.restaurant.restaurant_app.entity.RestaurantTable;
import com.restaurant.restaurant_app.entity.ServiceRequest;
import com.restaurant.restaurant_app.repository.CategoryRepository;
import com.restaurant.restaurant_app.repository.MenuItemRepository;
import com.restaurant.restaurant_app.repository.OrderRepository;
import com.restaurant.restaurant_app.repository.RestaurantTableRepository;
import com.restaurant.restaurant_app.repository.ServiceRequestRepository;
import com.restaurant.restaurant_app.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/t/{tableId}")
@RequiredArgsConstructor
public class GuestMenuController {

    private final RestaurantTableRepository tableRepository;
    private final CategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;
    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final ServiceRequestRepository serviceRequestRepository;

    @GetMapping("/welcome")
    public String welcome(@PathVariable Long tableId, Model model) {
        model.addAttribute("table", findTable(tableId));
        return "guest/welcome";
    }

    @GetMapping
    public String menu(@PathVariable Long tableId,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(defaultValue = "") String q,
                       HttpSession session,
                       Model model) {

        model.addAttribute("table", findTable(tableId));
        model.addAttribute("categories", categoryRepository.findAll(Sort.by("id")));
        model.addAttribute("items", menuItemRepository.search(categoryId, q.trim()));
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("q", q);
        addCart(model, session);
        return "guest/menu";
    }

    @GetMapping("/cart")
    public String cart(@PathVariable Long tableId, HttpSession session, Model model) {
        model.addAttribute("table", findTable(tableId));
        addCart(model, session);
        return "guest/cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@PathVariable Long tableId,
                            @RequestParam Long menuItemId,
                            @RequestParam(defaultValue = "1") int quantity,
                            HttpSession session) {
        cartService.add(session, menuItemId, Math.max(1, quantity));
        return "redirect:/t/" + tableId + "/cart";
    }

    @PostMapping("/cart/quantity")
    public String updateQuantity(@PathVariable Long tableId,
                                 @RequestParam Long menuItemId,
                                 @RequestParam int delta,
                                 HttpSession session) {
        cartService.changeQuantity(session, menuItemId, delta);
        return "redirect:/t/" + tableId + "/cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@PathVariable Long tableId,
                                 @RequestParam Long menuItemId,
                                 HttpSession session) {
        cartService.remove(session, menuItemId);
        return "redirect:/t/" + tableId + "/cart";
    }

    @PostMapping("/cart/submit")
    public String submit(@PathVariable Long tableId,
                         @RequestParam(required = false) String note,
                         HttpSession session,
                         Model model) {
        Order order;
        try {
            order = cartService.submit(session, tableId, note);
        } catch (ResponseStatusException e) {
            model.addAttribute("table", findTable(tableId));
            model.addAttribute("error", e.getReason());
            addCart(model, session);
            return "guest/cart";
        }
        return "redirect:/t/" + tableId + "?sent=" + order.getId();
    }

    @GetMapping("/orders")
    public String myOrders(@PathVariable Long tableId, HttpSession session, Model model) {
        RestaurantTable table = findTable(tableId);
        model.addAttribute("table", table);
        List<Order> orders = orderRepository.findByTableAndStatusNotOrderByCreatedAtAsc(
                table, OrderStatus.PAID);
        model.addAttribute("orders", orders);
        addCart(model, session);
        return "guest/my-orders";
    }

    @GetMapping("/service")
    public String servicePage(@PathVariable Long tableId, HttpSession session, Model model) {
        model.addAttribute("table", findTable(tableId));
        addCart(model, session);
        return "guest/service";
    }

    @PostMapping("/service")
    public String sendServiceRequest(@PathVariable Long tableId,
                                     @RequestParam String message,
                                     RedirectAttributes redirect) {
        RestaurantTable table = findTable(tableId);
        if (message == null || message.isBlank()) {
            redirect.addFlashAttribute("error", "Vui lòng nhập nội dung yêu cầu.");
            return "redirect:/t/" + tableId + "/service";
        }
        serviceRequestRepository.save(new ServiceRequest(table, message.trim()));
        redirect.addFlashAttribute("success", "Đã gửi yêu cầu! Nhân viên sẽ đến ngay.");
        return "redirect:/t/" + tableId + "/service";
    }

    private void addCart(Model model, HttpSession session) {
        List<CartLine> lines = cartService.get(session);
        model.addAttribute("cart", lines == null ? List.of() : lines);
        model.addAttribute("cartCount", lines == null ? 0 : lines.stream().mapToInt(CartLine::getQuantity).sum());
        model.addAttribute("cartTotal", cartService.total(lines == null ? List.of() : lines));
    }

    private RestaurantTable findTable(Long tableId) {
        return tableRepository.findById(tableId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy bàn"));
    }
}
