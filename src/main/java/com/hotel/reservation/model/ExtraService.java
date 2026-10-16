package com.hotel.reservation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;

@Embeddable
public class ExtraService {

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private ExtraServiceType type;

    @Column(name = "cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal cost;

    protected ExtraService() {
    }

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
