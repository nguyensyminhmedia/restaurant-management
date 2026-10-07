package com.restaurant.restaurant_app.controller;

import com.restaurant.restaurant_app.entity.Category;
import com.restaurant.restaurant_app.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/manager/categories")
@RequiredArgsConstructor
public class ManagerCategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryRepository.findAll(Sort.by("id")));
        return "manager/categories";
    }

    @PostMapping("/add")
    public String add(@RequestParam String name, RedirectAttributes redirect) {
        if (name.isBlank()) {
            redirect.addFlashAttribute("error", "Tên danh mục không được để trống.");
        } else {
            categoryRepository.save(new Category(name.trim()));
            redirect.addFlashAttribute("message", "Đã thêm danh mục: " + name.trim());
        }
        return "redirect:/manager/categories";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, @RequestParam String name,
                         RedirectAttributes redirect) {
        if (name.isBlank()) {
            redirect.addFlashAttribute("error", "Tên danh mục không được để trống.");
        } else {
            Category category = categoryRepository.findById(id).orElseThrow();
            category.setName(name.trim());
            categoryRepository.save(category);
            redirect.addFlashAttribute("message", "Đã cập nhật danh mục.");
        }
        return "redirect:/manager/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            categoryRepository.deleteById(id);
            redirect.addFlashAttribute("message", "Đã xóa danh mục.");
        } catch (DataIntegrityViolationException e) {
            redirect.addFlashAttribute("error",
                    "Danh mục này còn món ăn nên không xóa được. Hãy chuyển hoặc xóa các món trước.");
        }
        return "redirect:/manager/categories";
    }
}
