package com.hotel.reservation.controller;

import com.hotel.reservation.dto.ReservationRequest;
import com.hotel.reservation.dto.RoomAvailability;
import com.hotel.reservation.dto.ServiceRequest;
import com.hotel.reservation.facade.HotelFacade;
import com.hotel.reservation.model.Reservation;
import com.hotel.reservation.model.RoomType;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hotel")
public class HotelController {

    private final HotelFacade hotelFacade;

    public HotelController(HotelFacade hotelFacade) {
        this.hotelFacade = hotelFacade;
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
    public Reservation createReservation(@Valid @RequestBody ReservationRequest request) {
        return hotelFacade.createReservation(request);
    }

    @PostMapping("/services/{reservationId}")
    public Reservation addService(@PathVariable String reservationId, @Valid @RequestBody ServiceRequest request) {
        return hotelFacade.addService(reservationId, request.serviceType());
    }

    @PutMapping("/checkin/{reservationId}")
    public Reservation checkIn(@PathVariable String reservationId) {
        return hotelFacade.checkIn(reservationId);
    }

    @PutMapping("/checkout/{reservationId}")
    public Reservation checkOut(@PathVariable String reservationId) {
        return hotelFacade.checkOut(reservationId);
    }

    @GetMapping("/reservation/{reservationId}")
    public Reservation findReservation(@PathVariable String reservationId) {
        return hotelFacade.findReservation(reservationId);
    }
}
