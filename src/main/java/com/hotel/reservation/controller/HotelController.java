package com.hotel.reservation.controller;

import com.hotel.reservation.dto.BookingRequest;
import com.hotel.reservation.dto.RoomAvailability;
import com.hotel.reservation.dto.ServiceRequest;
import com.hotel.reservation.facade.HotelFacade;
import com.hotel.reservation.model.Reservation;
import com.hotel.reservation.model.RoomType;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hotel")
public class HotelController {

    private final HotelFacade hotelFacade;
    private final CurrentGuestResolver currentGuestResolver;

    public HotelController(HotelFacade hotelFacade, CurrentGuestResolver currentGuestResolver) {
        this.hotelFacade = hotelFacade;
        this.currentGuestResolver = currentGuestResolver;
    }

    @GetMapping("/availability")
    public List<RoomAvailability> checkAvailability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(required = false) RoomType type) {
        return hotelFacade.checkAvailability(checkIn, checkOut, type);
    }

    @PostMapping("/reserve")
    @ResponseStatus(HttpStatus.CREATED)
    public Reservation bookRoom(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                                @Valid @RequestBody BookingRequest request) {
        return hotelFacade.bookRoom(currentGuestResolver.resolve(authorization), request);
    }

    @GetMapping("/my-reservations")
    public List<Reservation> myReservations(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        return hotelFacade.findGuestReservations(currentGuestResolver.resolve(authorization));
    }

    @PostMapping("/services/{reservationId}")
    public Reservation addService(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                                  @PathVariable String reservationId, @Valid @RequestBody ServiceRequest request) {
        return hotelFacade.addService(currentGuestResolver.resolve(authorization), reservationId, request.serviceType());
    }

    @PutMapping("/checkin/{reservationId}")
    public Reservation checkIn(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                               @PathVariable String reservationId) {
        return hotelFacade.checkIn(currentGuestResolver.resolve(authorization), reservationId);
    }

    @PutMapping("/checkout/{reservationId}")
    public Reservation checkOut(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                                @PathVariable String reservationId) {
        return hotelFacade.checkOut(currentGuestResolver.resolve(authorization), reservationId);
    }

    @GetMapping("/reservation/{reservationId}")
    public Reservation findReservation(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @PathVariable String reservationId) {
        return hotelFacade.findReservation(currentGuestResolver.resolve(authorization), reservationId);
    }
}
