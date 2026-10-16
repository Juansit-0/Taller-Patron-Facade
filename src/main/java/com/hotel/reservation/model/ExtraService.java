package com.hotel.reservation.model;

import java.math.BigDecimal;

public class ExtraService {

    private final ExtraServiceType type;
    private final BigDecimal cost;

    public ExtraService(ExtraServiceType type, BigDecimal cost) {
        this.type = type;
        this.cost = cost;
    }

    public ExtraServiceType getType() {
        return type;
    }

    public BigDecimal getCost() {
        return cost;
    }
}
