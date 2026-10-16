package com.hotel.reservation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReservationRequest(
        @NotBlank(message = "Guest name is required") String guestName,
        @NotBlank(message = "Guest email is required") @Email(message = "Guest email is not valid") String guestEmail,
        @NotBlank(message = "Guest phone is required") String guestPhone,
        @NotBlank(message = "Document id is required") String documentId,
        @NotNull(message = "Room number is required") Integer roomNumber,
        @NotNull(message = "Check-in date is required") LocalDate checkIn,
        @NotNull(message = "Check-out date is required") LocalDate checkOut) {
}
