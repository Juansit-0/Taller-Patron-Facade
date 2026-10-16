package com.hotel.reservation.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hotel.reservation.dto.ReservationRequest;
import com.hotel.reservation.dto.RoomAvailability;
import com.hotel.reservation.exception.ReservationConflictException;
import com.hotel.reservation.model.ExtraServiceType;
import com.hotel.reservation.model.Reservation;
import com.hotel.reservation.model.ReservationStatus;
import com.hotel.reservation.model.RoomType;
import java.math.BigDecimal;
import java.time.LocalDate;
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

    @Test
    void completesFullStayAndReleasesRoom() {
        Reservation reservation = hotelFacade.createReservation(request(checkIn, checkOut));
        assertEquals(new BigDecimal("750.00"), reservation.getRoomTotal());

        hotelFacade.addService(reservation.getId(), ExtraServiceType.BREAKFAST);
        reservation = hotelFacade.addService(reservation.getId(), ExtraServiceType.SPA);
        assertEquals(new BigDecimal("30.00"), reservation.getServices().get(0).getCost());
        assertEquals(new BigDecimal("50.00"), reservation.getServices().get(1).getCost());

        reservation = hotelFacade.checkIn(reservation.getId());
        assertNotNull(reservation.getDigitalKey());
        assertEquals(ReservationStatus.CHECKED_IN, reservation.getStatus());

        reservation = hotelFacade.checkOut(reservation.getId());
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
        hotelFacade.createReservation(request(checkIn, checkOut));
        assertThrows(ReservationConflictException.class,
                () -> hotelFacade.createReservation(request(checkIn.plusDays(1), checkOut.plusDays(1))));
    }

    private ReservationRequest request(LocalDate from, LocalDate to) {
        return new ReservationRequest("Ana Torres", "ana@example.com", "3001234567", "1085123456", 303, from, to);
    }
}
