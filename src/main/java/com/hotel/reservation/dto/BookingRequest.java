package com.hotel.reservation.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record BookingRequest(
        @NotNull(message = "Room number is required") Integer roomNumber,
        @NotNull(message = "Check-in date is required") LocalDate checkIn,
        @NotNull(message = "Check-out date is required") LocalDate checkOut) {
}
