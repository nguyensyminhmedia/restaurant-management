package com.restaurant.restaurant_app.util;

import java.time.Duration;
import java.time.LocalDateTime;

public final class TimeAgo {

    private TimeAgo() {
    }

    public static String of(LocalDateTime time) {
        if (time == null) {
            return "";
        }
        Duration d = Duration.between(time, LocalDateTime.now());
        long minutes = Math.max(0, d.toMinutes());
        if (minutes < 1) {
            return "vừa xong";
        }
        if (minutes < 60) {
            return minutes + " phút";
        }
        long hours = minutes / 60;
        if (hours < 24) {
            return hours + " giờ";
        }
        return (hours / 24) + " ngày";
    }
}
