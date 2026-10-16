package com.hotel.reservation.controller;

import com.hotel.reservation.dto.AuthResponse;
import com.hotel.reservation.dto.LoginRequest;
import com.hotel.reservation.dto.ProfileRequest;
import com.hotel.reservation.dto.RegisterRequest;
import com.hotel.reservation.model.Guest;
import com.hotel.reservation.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final CurrentGuestResolver currentGuestResolver;

    public AuthController(AuthService authService, CurrentGuestResolver currentGuestResolver) {
        this.authService = authService;
        this.currentGuestResolver = currentGuestResolver;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        currentGuestResolver.resolve(authorization);
        authService.logout(currentGuestResolver.token(authorization));
    }

    @GetMapping("/me")
    public Guest me(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        return currentGuestResolver.resolve(authorization);
    }

    @PutMapping("/me")
    public Guest updateProfile(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                               @Valid @RequestBody ProfileRequest request) {
        return authService.updateProfile(currentGuestResolver.resolve(authorization), request);
    }
}
