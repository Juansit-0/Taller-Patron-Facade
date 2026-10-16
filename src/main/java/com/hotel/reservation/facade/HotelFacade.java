package com.hotel.reservation.facade;

import com.hotel.reservation.dto.BookingRequest;
import com.hotel.reservation.dto.RoomAvailability;
import com.hotel.reservation.exception.ForbiddenException;
import com.hotel.reservation.exception.InvalidReservationException;
import com.hotel.reservation.exception.ReservationConflictException;
import com.hotel.reservation.exception.ReservationNotFoundException;
import com.hotel.reservation.model.ExtraServiceType;
import com.hotel.reservation.model.Guest;
import com.hotel.reservation.model.Reservation;
import com.hotel.reservation.model.ReservationStatus;
import com.hotel.reservation.model.Room;
import com.hotel.reservation.model.RoomType;
import com.hotel.reservation.repository.ReservationRepository;
import com.hotel.reservation.service.AccessService;
import com.hotel.reservation.service.AmenityService;
import com.hotel.reservation.service.BillingService;
import com.hotel.reservation.service.RateService;
import com.hotel.reservation.service.RoomService;
import com.hotel.reservation.strategy.SeasonRateStrategy;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class HotelFacade {

    private final RoomService roomService;
    private final RateService rateService;
    private final AmenityService amenityService;
    private final BillingService billingService;
    private final AccessService accessService;
    private final ReservationRepository reservationRepository;

    public HotelFacade(RoomService roomService, RateService rateService, AmenityService amenityService,
                       BillingService billingService, AccessService accessService,
                       ReservationRepository reservationRepository) {
        this.roomService = roomService;
        this.rateService = rateService;
        this.amenityService = amenityService;
        this.billingService = billingService;
        this.accessService = accessService;
        this.reservationRepository = reservationRepository;
    }

    public List<RoomAvailability> checkAvailability(LocalDate checkIn, LocalDate checkOut, RoomType type) {
        long nights = validateDates(checkIn, checkOut);
        SeasonRateStrategy strategy = rateService.strategyFor(checkIn);
        return roomService.findAvailability(checkIn, checkOut, type).entrySet().stream()
                .map(entry -> {
                    Room room = entry.getKey();
                    return new RoomAvailability(room.getNumber(), room.getType(), room.getFloor(),
                            room.getBasePrice(), entry.getValue(), nights, strategy.season(),
                            rateService.calculateTotal(room, checkIn, nights));
                })
                .toList();
    }

    public Reservation bookRoom(Guest guest, BookingRequest request) {
        long nights = validateDates(request.checkIn(), request.checkOut());
        Room room = roomService.findRoom(request.roomNumber());
        Reservation reservation = new Reservation(
                "RSV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                guest, room, request.checkIn(), request.checkOut(), nights,
                rateService.strategyFor(request.checkIn()).season(),
                rateService.nightlyRate(room, request.checkIn()),
                rateService.calculateTotal(room, request.checkIn(), nights));
        return roomService.reserve(reservation);
    }

    public List<Reservation> findGuestReservations(Guest guest) {
        return reservationRepository.findByGuestIdOrderByCheckInDesc(guest.getId());
    }

    @Transactional
    public Reservation addService(Guest guest, String reservationId, ExtraServiceType serviceType) {
        Reservation reservation = findReservation(guest, reservationId);
        if (!reservation.holdsRoom()) {
            throw new ReservationConflictException("Services cannot be added to a reservation that is " + reservation.getStatus());
        }
        reservation.addService(amenityService.createService(serviceType, reservation.getNights()));
        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation checkIn(Guest guest, String reservationId) {
        Reservation reservation = findReservation(guest, reservationId);
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new ReservationConflictException("Check-in requires a CONFIRMED reservation, current status is " + reservation.getStatus());
        }
        reservation.setDigitalKey(accessService.generateKey());
        reservation.setStatus(ReservationStatus.CHECKED_IN);
        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation checkOut(Guest guest, String reservationId) {
        Reservation reservation = findReservation(guest, reservationId);
        if (reservation.getStatus() != ReservationStatus.CHECKED_IN) {
            throw new ReservationConflictException("Check-out requires a CHECKED_IN reservation, current status is " + reservation.getStatus());
        }
        reservation.setInvoice(billingService.generateInvoice(reservation));
        return roomService.release(reservation);
    }

    public Reservation findReservation(Guest guest, String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation " + reservationId + " not found"));
        if (!reservation.belongsTo(guest)) {
            throw new ForbiddenException("Esta reserva no pertenece a tu cuenta");
        }
        return reservation;
    }

    private long validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn.isBefore(LocalDate.now())) {
            throw new InvalidReservationException("Check-in date cannot be in the past");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new InvalidReservationException("Check-out date must be after check-in date");
        }
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }
}
