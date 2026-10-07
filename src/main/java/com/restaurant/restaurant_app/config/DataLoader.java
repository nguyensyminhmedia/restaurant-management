package com.restaurant.restaurant_app.config;

import com.restaurant.restaurant_app.entity.*;
import com.restaurant.restaurant_app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;
    private final RestaurantTableRepository tableRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        userRepository.save(new User("admin", passwordEncoder.encode("123456"), "Anh Minh Quản Lý", Role.MANAGER));
        userRepository.save(new User("bep1", passwordEncoder.encode("123456"), "Nhật Đầu Bếp", Role.KITCHEN));
        userRepository.save(new User("thungan1", passwordEncoder.encode("123456"), "Quân Thu Ngân", Role.CASHIER));

        Category monChinh = categoryRepository.save(new Category("Món chính"));
        Category doUong = categoryRepository.save(new Category("Đồ uống"));
        Category trangMieng = categoryRepository.save(new Category("Tráng miệng"));

        menuItemRepository.save(new MenuItem(monChinh, "Phở bò", 55000));
        menuItemRepository.save(new MenuItem(monChinh, "Cơm gà xối mỡ", 50000));
        menuItemRepository.save(new MenuItem(monChinh, "Bún chả", 45000));
        menuItemRepository.save(new MenuItem(doUong, "Trà đá", 5000));
        menuItemRepository.save(new MenuItem(doUong, "Cà phê sữa", 25000));
        menuItemRepository.save(new MenuItem(trangMieng, "Chè thái", 30000));

        tableRepository.save(new RestaurantTable("Bàn 1", 4));
        tableRepository.save(new RestaurantTable("Bàn 2", 4));
        tableRepository.save(new RestaurantTable("Bàn 3", 6));
        tableRepository.save(new RestaurantTable("Bàn 4", 2));

        System.out.println(">>> Da nap du lieu mau: "
                + userRepository.count() + " nguoi dung, "
                + menuItemRepository.count() + " mon, "
                + tableRepository.count() + " ban");
    }
}
