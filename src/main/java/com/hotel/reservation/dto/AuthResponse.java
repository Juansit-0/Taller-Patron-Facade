package com.hotel.reservation.dto;

import com.hotel.reservation.model.Guest;

public record AuthResponse(String token, Guest guest) {
}
