package com.restaurant.restaurant_app.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class CartLine implements Serializable {

    private Long menuItemId;
    private String name;
    private long price;
    private int quantity;

    public CartLine(Long menuItemId, String name, long price, int quantity) {
        this.menuItemId = menuItemId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public long getSubtotal() {
        return price * quantity;
    }
}
