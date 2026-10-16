package com.hotel.reservation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @Column(name = "room_number")
    private int number;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false)
    private RoomType type;

    @Column(name = "floor_number", nullable = false)
    private int floor;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    protected Room() {
    }

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
