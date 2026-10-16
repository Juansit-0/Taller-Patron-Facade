package com.hotel.reservation.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @Column(name = "invoice_number")
    private String number;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "guest_name", nullable = false)
    private String guestName;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "invoice_lines", joinColumns = @JoinColumn(name = "invoice_number"))
    @OrderColumn(name = "line_order")
    private List<InvoiceLine> lines = new ArrayList<>();

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    protected Invoice() {
    }

    public Invoice(String number, LocalDateTime issuedAt, String guestName,
                   List<InvoiceLine> lines, BigDecimal subtotal, BigDecimal total) {
        this.number = number;
        this.issuedAt = issuedAt;
        this.guestName = guestName;
        this.lines = new ArrayList<>(lines);
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
