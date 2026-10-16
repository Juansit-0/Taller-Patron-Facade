package com.hotel.reservation.dto;

import jakarta.validation.constraints.NotBlank;

public record ProfileRequest(
        @NotBlank(message = "El nombre completo es obligatorio") String fullName,
        @NotBlank(message = "El telefono es obligatorio") String phone,
        @NotBlank(message = "El documento es obligatorio") String documentId) {
}
