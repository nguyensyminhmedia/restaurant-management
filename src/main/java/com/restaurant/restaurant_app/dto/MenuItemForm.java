package com.restaurant.restaurant_app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuItemForm {
    private Long id;
    private String name;
    private Long price;
    private String imageUrl;
    private Long categoryId;
    private boolean available = true;
}
