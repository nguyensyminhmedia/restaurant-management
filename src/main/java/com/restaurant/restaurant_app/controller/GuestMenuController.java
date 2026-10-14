package com.restaurant.restaurant_app.controller;

import com.restaurant.restaurant_app.entity.RestaurantTable;
import com.restaurant.restaurant_app.repository.CategoryRepository;
import com.restaurant.restaurant_app.repository.MenuItemRepository;
import com.restaurant.restaurant_app.repository.RestaurantTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/t/{tableId}")
@RequiredArgsConstructor
public class GuestMenuController {

    private final RestaurantTableRepository tableRepository;
    private final CategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;

    @GetMapping
    public String menu(@PathVariable Long tableId,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(defaultValue = "") String q,
                       Model model) {

        RestaurantTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy bàn"));

        model.addAttribute("table", table);
        model.addAttribute("categories", categoryRepository.findAll(Sort.by("id")));
        model.addAttribute("items", menuItemRepository.search(categoryId, q.trim()));
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("q", q);
        return "guest/menu";
    }
}
