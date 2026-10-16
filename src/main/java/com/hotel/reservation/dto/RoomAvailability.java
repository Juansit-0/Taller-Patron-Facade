package com.hotel.reservation.dto;

import com.hotel.reservation.model.RoomType;
import com.hotel.reservation.model.SeasonType;
import java.math.BigDecimal;

public record RoomAvailability(
        int number,
        RoomType type,
        int floor,
        BigDecimal basePrice,
        boolean available,
        long nights,
        SeasonType season,
        BigDecimal totalPrice) {
}
