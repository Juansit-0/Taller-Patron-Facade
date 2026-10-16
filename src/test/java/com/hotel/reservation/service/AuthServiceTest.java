package com.hotel.reservation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hotel.reservation.dto.AuthResponse;
import com.hotel.reservation.dto.LoginRequest;
import com.hotel.reservation.dto.RegisterRequest;
import com.hotel.reservation.exception.AccountConflictException;
import com.hotel.reservation.exception.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    void loginReturnsTokenThatResolvesTheGuest() {
        AuthResponse registered = authService.register(request("Maria@Example.com"));

        AuthResponse loggedIn = authService.login(new LoginRequest("maria@example.com", "secret123"));

        assertEquals("maria@example.com", registered.guest().getEmail());
        assertEquals(registered.guest().getId(), authService.findGuestByToken(loggedIn.token()).getId());
    }

    @Test
    void rejectsWrongPassword() {
        authService.register(request("maria@example.com"));
        assertThrows(UnauthorizedException.class,
                () -> authService.login(new LoginRequest("maria@example.com", "wrong-password")));
    }

    @Test
    void rejectsDuplicateEmail() {
        authService.register(request("maria@example.com"));
        assertThrows(AccountConflictException.class, () -> authService.register(request("MARIA@example.com")));
    }

    private RegisterRequest request(String email) {
        return new RegisterRequest("Maria Gomez", email, "3009876543", "1085654321", "secret123");
    }
}
