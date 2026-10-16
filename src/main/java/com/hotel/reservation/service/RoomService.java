package com.hotel.reservation.service;

import com.hotel.reservation.exception.ReservationConflictException;
import com.hotel.reservation.exception.ReservationNotFoundException;
import com.hotel.reservation.factory.RoomFactory;
import com.hotel.reservation.model.Reservation;
import com.hotel.reservation.model.ReservationStatus;
import com.hotel.reservation.model.Room;
import com.hotel.reservation.model.RoomType;
import com.hotel.reservation.repository.ReservationRepository;
import com.hotel.reservation.repository.RoomRepository;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class RoomService {

    private static final List<ReservationStatus> ACTIVE_STATUSES =
            List.of(ReservationStatus.CONFIRMED, ReservationStatus.CHECKED_IN);

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public RoomService(RoomRepository roomRepository, ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
    }

    @PostConstruct
    public void seedRooms() {
        if (roomRepository.count() == 0) {
            roomRepository.saveAll(RoomFactory.createRooms());
        }
    }

    public Map<Room, Boolean> findAvailability(LocalDate checkIn, LocalDate checkOut, RoomType type) {
        Map<Room, Boolean> availability = new LinkedHashMap<>();
        for (Room room : roomRepository.findAll(Sort.by("number"))) {
            if (type == null || room.getType() == type) {
                availability.put(room, isAvailable(room, checkIn, checkOut));
            }
        }
        return availability;
    }

    public Room findRoom(int number) {
        return roomRepository.findById(number)
                .orElseThrow(() -> new ReservationNotFoundException("Room " + number + " does not exist"));
    }

    public synchronized Reservation reserve(Reservation reservation) {
        if (!isAvailable(reservation.getRoom(), reservation.getCheckIn(), reservation.getCheckOut())) {
            throw new ReservationConflictException(
                    "Room " + reservation.getRoom().getNumber() + " is already booked for those dates");
        }
        return reservationRepository.save(reservation);
    }

    public Reservation release(Reservation reservation) {
        reservation.setStatus(ReservationStatus.CHECKED_OUT);
        return reservationRepository.save(reservation);
    }

    private boolean isAvailable(Room room, LocalDate checkIn, LocalDate checkOut) {
        return !reservationRepository.existsOverlapping(room.getNumber(), ACTIVE_STATUSES, checkIn, checkOut);
    }
}
