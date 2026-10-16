package com.hotel.reservation.builder;

import com.hotel.reservation.model.Invoice;
import com.hotel.reservation.model.InvoiceLine;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InvoiceBuilder {

    private String guestName;
    private final List<InvoiceLine> lines = new ArrayList<>();

    public InvoiceBuilder guestName(String guestName) {
        this.guestName = guestName;
        return this;
    }

    public InvoiceBuilder addLine(String description, long quantity, BigDecimal unitPrice) {
        lines.add(new InvoiceLine(description, quantity, unitPrice));
        return this;
    }

    public Invoice build() {
        BigDecimal subtotal = lines.stream()
                .map(InvoiceLine::getAmount)
                .reduce(BigDecimal.ZERO.setScale(2), BigDecimal::add);
        String number = "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new Invoice(number, LocalDateTime.now(), guestName, List.copyOf(lines), subtotal, subtotal);
    }
}
