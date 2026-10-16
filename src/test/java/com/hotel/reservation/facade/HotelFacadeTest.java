package com.hotel.reservation.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hotel.reservation.dto.BookingRequest;
import com.hotel.reservation.dto.RegisterRequest;
import com.hotel.reservation.dto.RoomAvailability;
import com.hotel.reservation.exception.ForbiddenException;
import com.hotel.reservation.exception.ReservationConflictException;
import com.hotel.reservation.model.ExtraServiceType;
import com.hotel.reservation.model.Guest;
import com.hotel.reservation.model.Reservation;
import com.hotel.reservation.model.ReservationStatus;
import com.hotel.reservation.model.RoomType;
import com.hotel.reservation.service.AuthService;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class HotelFacadeTest {

    private final LocalDate checkIn = LocalDate.of(LocalDate.now().getYear() + 1, 7, 10);
    private final LocalDate checkOut = checkIn.plusDays(2);

    @Autowired
    private HotelFacade hotelFacade;

    @Autowired
    private AuthService authService;

    private Guest guest;

    @BeforeEach
    void registerGuest() {
        guest = register("ana@example.com");
    }

    @Test
    void completesFullStayAndReleasesRoom() {
        Reservation reservation = hotelFacade.bookRoom(guest, new BookingRequest(303, checkIn, checkOut));
        assertEquals(new BigDecimal("750.00"), reservation.getRoomTotal());
        assertEquals("Ana Torres", reservation.getGuestName());
        assertEquals(1, hotelFacade.findGuestReservations(guest).size());

        hotelFacade.addService(guest, reservation.getId(), ExtraServiceType.BREAKFAST);
        reservation = hotelFacade.addService(guest, reservation.getId(), ExtraServiceType.SPA);
        assertEquals(new BigDecimal("30.00"), reservation.getServices().get(0).getCost());
        assertEquals(new BigDecimal("50.00"), reservation.getServices().get(1).getCost());

        reservation = hotelFacade.checkIn(guest, reservation.getId());
        assertNotNull(reservation.getDigitalKey());
        assertEquals(ReservationStatus.CHECKED_IN, reservation.getStatus());

        reservation = hotelFacade.checkOut(guest, reservation.getId());
        assertEquals(new BigDecimal("830.00"), reservation.getInvoice().getTotal());
        assertEquals(ReservationStatus.CHECKED_OUT, reservation.getStatus());

        RoomAvailability suite = hotelFacade.checkAvailability(checkIn, checkOut, RoomType.SUITE).stream()
                .filter(room -> room.number() == 303)
                .findFirst()
                .orElseThrow();
        assertTrue(suite.available());
    }

    @Test
    void rejectsOverlappingReservation() {
        hotelFacade.bookRoom(guest, new BookingRequest(303, checkIn, checkOut));
        assertThrows(ReservationConflictException.class,
                () -> hotelFacade.bookRoom(guest, new BookingRequest(303, checkIn.plusDays(1), checkOut.plusDays(1))));
    }

    @Test
    void rejectsCheckInFromAnotherGuest() {
        Reservation reservation = hotelFacade.bookRoom(guest, new BookingRequest(303, checkIn, checkOut));
        Guest otherGuest = register("luis@example.com");
        assertThrows(ForbiddenException.class, () -> hotelFacade.checkIn(otherGuest, reservation.getId()));
    }

    private Guest register(String email) {
        return authService.register(new RegisterRequest("Ana Torres", email, "3001234567", "1085123456", "secret123"))
                .guest();
    }
}
