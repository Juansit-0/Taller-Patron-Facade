package com.hotel.reservation.model;

import java.math.BigDecimal;

public class InvoiceLine {

    private final String description;
    private final long quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal amount;

    public InvoiceLine(String description, long quantity, BigDecimal unitPrice) {
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.amount = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public String getDescription() {
        return description;
    }

    public long getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
