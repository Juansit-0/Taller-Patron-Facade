package com.hotel.reservation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "El nombre completo es obligatorio") String fullName,
        @NotBlank(message = "El correo es obligatorio") @Email(message = "El correo no es valido") String email,
        @NotBlank(message = "El telefono es obligatorio") String phone,
        @NotBlank(message = "El documento es obligatorio") String documentId,
        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 6, message = "La contrasena debe tener al menos 6 caracteres") String password) {
}
