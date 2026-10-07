package com.restaurant.restaurant_app.controller;

import com.restaurant.restaurant_app.dto.MenuItemForm;
import com.restaurant.restaurant_app.entity.MenuItem;
import com.restaurant.restaurant_app.repository.CategoryRepository;
import com.restaurant.restaurant_app.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/manager/menu-items")
@RequiredArgsConstructor
public class ManagerMenuController {

    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("items", menuItemRepository.findAll(Sort.by("id")));
        return "manager/menu-items";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new MenuItemForm());
        model.addAttribute("categories", categoryRepository.findAll());
        return "manager/menu-item-form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        MenuItem item = menuItemRepository.findById(id).orElseThrow();
        MenuItemForm form = new MenuItemForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setImageUrl(item.getImageUrl());
        form.setCategoryId(item.getCategory().getId());
        form.setAvailable(item.isAvailable());
        model.addAttribute("form", form);
        model.addAttribute("categories", categoryRepository.findAll());
        return "manager/menu-item-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("form") MenuItemForm form,
                       Model model, RedirectAttributes redirect) {
        String error = validate(form);
        if (error != null) {
            model.addAttribute("error", error);
            model.addAttribute("categories", categoryRepository.findAll());
            return "manager/menu-item-form";
        }

        MenuItem item = (form.getId() == null)
                ? new MenuItem()
                : menuItemRepository.findById(form.getId()).orElseThrow();
        item.setName(form.getName().trim());
        item.setPrice(form.getPrice());
        item.setImageUrl(form.getImageUrl());
        item.setAvailable(form.isAvailable());
        item.setCategory(categoryRepository.findById(form.getCategoryId()).orElseThrow());
        menuItemRepository.save(item);

        redirect.addFlashAttribute("message", "Đã lưu món: " + item.getName());
        return "redirect:/manager/menu-items";
    }

    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id) {
        MenuItem item = menuItemRepository.findById(id).orElseThrow();
        item.setAvailable(!item.isAvailable());
        menuItemRepository.save(item);
        return "redirect:/manager/menu-items";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            menuItemRepository.deleteById(id);
            redirect.addFlashAttribute("message", "Đã xóa món.");
        } catch (DataIntegrityViolationException e) {
            redirect.addFlashAttribute("error",
                    "Món này đã có trong đơn hàng nên không xóa được. Hãy gạt sang Hết để ẩn món.");
        }
        return "redirect:/manager/menu-items";
    }

    private String validate(MenuItemForm f) {
        if (f.getName() == null || f.getName().isBlank()) return "Tên món không được để trống.";
        if (f.getPrice() == null || f.getPrice() <= 0) return "Giá phải lớn hơn 0.";
        if (f.getCategoryId() == null) return "Vui lòng chọn danh mục.";
        return null;
    }
}
