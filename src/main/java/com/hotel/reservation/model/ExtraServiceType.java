package com.hotel.reservation.model;

import java.math.BigDecimal;

public enum ExtraServiceType {
    SPA(new BigDecimal("50.00"), false),
    BREAKFAST(new BigDecimal("15.00"), true),
    TRANSFER(new BigDecimal("30.00"), false);

    private final BigDecimal price;
    private final boolean chargedPerNight;

    ExtraServiceType(BigDecimal price, boolean chargedPerNight) {
        this.price = price;
        this.chargedPerNight = chargedPerNight;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public long quantityFor(long nights) {
        return chargedPerNight ? nights : 1;
    }
}
