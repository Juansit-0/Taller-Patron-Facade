package com.hotel.reservation.dto;

import com.hotel.reservation.model.ExtraServiceType;
import jakarta.validation.constraints.NotNull;

public record ServiceRequest(@NotNull(message = "Service type is required") ExtraServiceType serviceType) {
}
