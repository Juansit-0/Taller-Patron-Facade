package com.hotel.reservation.dto;

import com.hotel.reservation.model.Reservation;

public record ChatResponse(String conversationId, String reply, Reservation reservation) {
}
