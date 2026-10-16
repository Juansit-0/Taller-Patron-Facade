package com.hotel.reservation.model;

import java.math.BigDecimal;

public class Room {

    private final int number;
    private final RoomType type;
    private final int floor;
    private final BigDecimal basePrice;

    public Room(int number, RoomType type, int floor) {
        this.number = number;
        this.type = type;
        this.floor = floor;
        this.basePrice = type.getBasePrice();
    }

    public int getNumber() {
        return number;
    }

    public RoomType getType() {
        return type;
    }

    public int getFloor() {
        return floor;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }
}
