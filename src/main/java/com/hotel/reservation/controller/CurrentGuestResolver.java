package com.hotel.reservation.controller;

import com.hotel.reservation.exception.UnauthorizedException;
import com.hotel.reservation.model.Guest;
import com.hotel.reservation.service.AuthService;
import org.springframework.stereotype.Component;

@Component
public class CurrentGuestResolver {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthService authService;

    public CurrentGuestResolver(AuthService authService) {
        this.authService = authService;
    }

    public Guest resolve(String authorizationHeader) {
        return authService.findGuestByToken(token(authorizationHeader));
    }

    public String token(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)
                || authorizationHeader.substring(BEARER_PREFIX.length()).isBlank()) {
            throw new UnauthorizedException("Debes iniciar sesion para continuar");
        }
        return authorizationHeader.substring(BEARER_PREFIX.length()).trim();
    }
}
