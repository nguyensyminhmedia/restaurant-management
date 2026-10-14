package com.restaurant.restaurant_app.controller;

import com.restaurant.restaurant_app.entity.Role;
import com.restaurant.restaurant_app.entity.User;
import com.restaurant.restaurant_app.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserRepository userRepository;

    @GetMapping("/")
    public void home(Authentication authentication, HttpServletResponse response) throws IOException {
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        String target = (user.getRole() == Role.KITCHEN) ? "/kitchen"
                : (user.getRole() == Role.CASHIER) ? "/cashier"
                : "/dashboard";
        response.sendRedirect(target);
    }
}