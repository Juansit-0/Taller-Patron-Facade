package com.hotel.reservation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "guest_sessions")
public class GuestSession {

    @Id
    @Column(name = "token")
    private String token;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected GuestSession() {
    }

    public GuestSession(Guest guest) {
        this.token = UUID.randomUUID().toString();
        this.guest = guest;
        this.createdAt = LocalDateTime.now();
    }

    public String getToken() {
        return token;
    }

    public Guest getGuest() {
        return guest;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
