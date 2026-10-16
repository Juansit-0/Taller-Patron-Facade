package com.hotel.reservation.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Invoice {

    private final String number;
    private final LocalDateTime issuedAt;
    private final String guestName;
    private final List<InvoiceLine> lines;
    private final BigDecimal subtotal;
    private final BigDecimal total;

    public Invoice(String number, LocalDateTime issuedAt, String guestName,
                   List<InvoiceLine> lines, BigDecimal subtotal, BigDecimal total) {
        this.number = number;
        this.issuedAt = issuedAt;
        this.guestName = guestName;
        this.lines = lines;
        this.subtotal = subtotal;
        this.total = total;
    }

    public String getNumber() {
        return number;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public String getGuestName() {
        return guestName;
    }

    public List<InvoiceLine> getLines() {
        return lines;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getTotal() {
        return total;
    }
}
