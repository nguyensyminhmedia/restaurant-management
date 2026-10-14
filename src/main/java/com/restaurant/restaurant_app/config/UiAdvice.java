package com.restaurant.restaurant_app.config;

import com.restaurant.restaurant_app.entity.Role;
import com.restaurant.restaurant_app.entity.User;
import com.restaurant.restaurant_app.repository.UserRepository;
import com.restaurant.restaurant_app.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class UiAdvice {

    private final UserRepository userRepository;
    private final DashboardService dashboardService;

    @ModelAttribute("roleName")
    public String roleName(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        return userRepository.findByUsername(authentication.getName())
                .map(u -> u.getRole().name())
                .orElse(null);
    }

    @ModelAttribute("fullName")
    public String fullName(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        return userRepository.findByUsername(authentication.getName())
                .map(User::getFullName)
                .orElse(authentication.getName());
    }

    @ModelAttribute("isManager")
    public boolean isManager(Authentication authentication) {
        return roleFrom(authentication) == Role.MANAGER;
    }

    @ModelAttribute("isKitchen")
    public boolean isKitchen(Authentication authentication) {
        return canAccessKitchen(authentication);
    }

    @ModelAttribute("isCashier")
    public boolean isCashier(Authentication authentication) {
        return canAccessCashier(authentication);
    }

    @ModelAttribute("alertCount")
    public long alertCount() {
        try {
            return dashboardService.openRequestCount();
        } catch (Exception e) {
            return 0;
        }
    }

    private boolean canAccessKitchen(Authentication a) {
        Role r = roleFrom(a);
        return r == Role.KITCHEN || r == Role.MANAGER;
    }

    private boolean canAccessCashier(Authentication a) {
        Role r = roleFrom(a);
        return r == Role.CASHIER || r == Role.MANAGER;
    }

    private Role roleFrom(Authentication a) {
        if (a == null || !a.isAuthenticated() || "anonymousUser".equals(a.getPrincipal())) {
            return null;
        }
        return userRepository.findByUsername(a.getName()).map(User::getRole).orElse(null);
    }
}
