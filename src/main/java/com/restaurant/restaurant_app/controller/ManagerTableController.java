package com.restaurant.restaurant_app.controller;

import com.restaurant.restaurant_app.entity.RestaurantTable;
import com.restaurant.restaurant_app.entity.TableStatus;
import com.restaurant.restaurant_app.repository.RestaurantTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/manager/tables")
@RequiredArgsConstructor
public class ManagerTableController {

    private final RestaurantTableRepository tableRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("tables", tableRepository.findAll(Sort.by("id")));
        return "manager/tables";
    }

    @PostMapping("/add")
    public String add(@RequestParam String name, @RequestParam int seats,
                      RedirectAttributes redirect) {
        String error = validate(name, seats);
        if (error != null) {
            redirect.addFlashAttribute("error", error);
        } else {
            tableRepository.save(new RestaurantTable(name.trim(), seats));
            redirect.addFlashAttribute("message", "Đã thêm: " + name.trim());
        }
        return "redirect:/manager/tables";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, @RequestParam String name,
                         @RequestParam int seats, RedirectAttributes redirect) {
        String error = validate(name, seats);
        if (error != null) {
            redirect.addFlashAttribute("error", error);
        } else {
            RestaurantTable table = tableRepository.findById(id).orElseThrow();
            table.setName(name.trim());
            table.setSeats(seats);
            tableRepository.save(table);
            redirect.addFlashAttribute("message", "Đã cập nhật bàn.");
        }
        return "redirect:/manager/tables";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        RestaurantTable table = tableRepository.findById(id).orElseThrow();
        if (table.getStatus() != TableStatus.EMPTY) {
            redirect.addFlashAttribute("error", "Bàn đang có khách hoặc đã đặt, không xóa được.");
            return "redirect:/manager/tables";
        }
        try {
            tableRepository.deleteById(id);
            redirect.addFlashAttribute("message", "Đã xóa bàn.");
        } catch (DataIntegrityViolationException e) {
            redirect.addFlashAttribute("error", "Bàn này đã có lịch sử đơn hàng nên không xóa được.");
        }
        return "redirect:/manager/tables";
    }

    private String validate(String name, int seats) {
        if (name.isBlank()) return "Tên bàn không được để trống.";
        if (seats <= 0) return "Số chỗ ngồi phải lớn hơn 0.";
        return null;
    }
}
