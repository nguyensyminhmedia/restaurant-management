package com.restaurant.restaurant_app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "restaurant_tables")
@Getter
@Setter
@NoArgsConstructor
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    private int seats;

    private String location;

    @Enumerated(EnumType.STRING)
    private TableStatus status = TableStatus.EMPTY;

    public RestaurantTable(String name, int seats) {
        this.name = name;
        this.seats = seats;
    }

    @Transient
    public String getDisplayName() {
        String compact = name.replaceAll("\\s+", "");
        String prefix = compact.startsWith("Ban") ? compact : ("Bàn " + compact);
        return prefix;
    }
}
