package com.hotel.reservation.service;

import com.hotel.reservation.dto.AuthResponse;
import com.hotel.reservation.dto.LoginRequest;
import com.hotel.reservation.dto.ProfileRequest;
import com.hotel.reservation.dto.RegisterRequest;
import com.hotel.reservation.exception.AccountConflictException;
import com.hotel.reservation.exception.UnauthorizedException;
import com.hotel.reservation.model.Guest;
import com.hotel.reservation.model.GuestSession;
import com.hotel.reservation.repository.GuestRepository;
import com.hotel.reservation.repository.GuestSessionRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final GuestRepository guestRepository;
    private final GuestSessionRepository guestSessionRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(GuestRepository guestRepository, GuestSessionRepository guestSessionRepository) {
        this.guestRepository = guestRepository;
        this.guestSessionRepository = guestSessionRepository;
    }

    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (guestRepository.findByEmail(email).isPresent()) {
            throw new AccountConflictException("Ya existe una cuenta con ese correo");
        }
        Guest guest = guestRepository.save(new Guest(request.fullName().trim(), email, request.phone().trim(),
                request.documentId().trim(), passwordEncoder.encode(request.password())));
        return openSession(guest);
    }

    public AuthResponse login(LoginRequest request) {
        return guestRepository.findByEmail(normalizeEmail(request.email()))
                .filter(guest -> passwordEncoder.matches(request.password(), guest.getPasswordHash()))
                .map(this::openSession)
                .orElseThrow(() -> new UnauthorizedException("Correo o contrasena incorrectos"));
    }

    public void logout(String token) {
        guestSessionRepository.deleteById(token);
    }

    public Guest findGuestByToken(String token) {
        return guestSessionRepository.findById(token)
                .map(GuestSession::getGuest)
                .orElseThrow(() -> new UnauthorizedException("Tu sesion no es valida, inicia sesion de nuevo"));
    }

    public Guest updateProfile(Guest guest, ProfileRequest request) {
        guest.updateProfile(request.fullName().trim(), request.phone().trim(), request.documentId().trim());
        return guestRepository.save(guest);
    }

    private AuthResponse openSession(Guest guest) {
        GuestSession session = guestSessionRepository.save(new GuestSession(guest));
        return new AuthResponse(session.getToken(), guest);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
