package com.restaurant.restaurant_app.controller;

import com.restaurant.restaurant_app.entity.Order;
import com.restaurant.restaurant_app.entity.ServiceRequest;
import com.restaurant.restaurant_app.service.DashboardService;
import com.restaurant.restaurant_app.util.TimeAgo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public String dashboard(@RequestParam(defaultValue = "") String q,
                            Authentication authentication,
                            Model model) {
        String name = authentication == null ? "" : authentication.getName();
        if (q != null && !q.isBlank()) {
            model.addAttribute("q", q);
            model.addAttribute("searchResults",
                    dashboardService.recentOrders(10).stream()
                            .filter(o -> o.getCode().toLowerCase(Locale.ROOT).contains(q.trim().toLowerCase(Locale.ROOT))
                                    || o.getTable().getName().toLowerCase(Locale.ROOT).contains(q.trim().toLowerCase(Locale.ROOT)))
                            .toList());
        }

        model.addAttribute("revenueToday", dashboardService.revenueToday());
        model.addAttribute("openOrderCount", dashboardService.openOrderCount());
        model.addAttribute("waitingKitchenCount", dashboardService.waitingKitchenCount());
        model.addAttribute("tablesInUse", dashboardService.tablesInUse());
        model.addAttribute("tableCount", dashboardService.tableCount());
        model.addAttribute("occupancy", dashboardService.occupancyPercent());
        model.addAttribute("recentOrders", dashboardService.recentOrders(5));
        model.addAttribute("activity", buildActivity());
        model.addAttribute("dateLine", dateLine());
        model.addAttribute("shift", shiftOf());
        return "dashboard/overview";
    }

    private List<ActivityLine> buildActivity() {
        List<ActivityLine> out = new ArrayList<>();
        List<Order> recent = dashboardService.recentOrders(10);
        recent.stream()
                .filter(o -> o.getStatus() == com.restaurant.restaurant_app.entity.OrderStatus.PAID)
                .limit(2)
                .forEach(o -> out.add(new ActivityLine(
                        "check",
                        o.getTable().getName() + " đã thanh toán",
                        "Tiền mặt · " + String.format("%,d", o.getTotal()) + "đ",
                        TimeAgo.of(o.getCreatedAt()))));
        for (ServiceRequest r : dashboardService.openRequests().stream().limit(2).toList()) {
            out.add(new ActivityLine(
                    "bell",
                    r.getTable().getName() + " gọi nhân viên",
                    r.getMessage(),
                    TimeAgo.of(r.getCreatedAt())));
        }
        dashboardService.recentOrders(10).stream()
                .filter(o -> o.getStatus() == com.restaurant.restaurant_app.entity.OrderStatus.READY)
                .limit(1)
                .forEach(o -> out.add(new ActivityLine(
                        "chef",
                        "Bếp hoàn thành " + o.getItems().size() + " món",
                        o.getCode() + " · " + o.getTable().getName(),
                        TimeAgo.of(o.getCreatedAt()))));
        return out;
    }

    private String dateLine() {
        LocalDate today = LocalDate.now();
        String day = switch (today.getDayOfWeek()) {
            case MONDAY -> "Thứ Hai";
            case TUESDAY -> "Thứ Ba";
            case WEDNESDAY -> "Thứ Tư";
            case THURSDAY -> "Thứ Năm";
            case FRIDAY -> "Thứ Sáu";
            case SATURDAY -> "Thứ Bảy";
            case SUNDAY -> "Chủ Nhật";
        };
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return day + ", " + today.format(f) + " · Chi nhánh Quận 1";
    }

    private String shiftOf() {
        return LocalDate.now().getDayOfWeek() == DayOfWeek.SUNDAY ? "Ca cuối tuần" : "Ca trưa · 11:00 – 15:00";
    }

    public record ActivityLine(String icon, String title, String sub, String ago) {
    }
}
