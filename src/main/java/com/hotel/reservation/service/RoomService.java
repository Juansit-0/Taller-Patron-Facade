package com.hotel.reservation.service;

import com.hotel.reservation.exception.ReservationConflictException;
import com.hotel.reservation.exception.ReservationNotFoundException;
import com.hotel.reservation.factory.RoomFactory;
import com.hotel.reservation.model.Reservation;
import com.hotel.reservation.model.Room;
import com.hotel.reservation.model.RoomType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class RoomService {

    private final List<Room> rooms = RoomFactory.createRooms();
    private final List<Reservation> bookings = new ArrayList<>();

    public synchronized Map<Room, Boolean> findAvailability(LocalDate checkIn, LocalDate checkOut, RoomType type) {
        Map<Room, Boolean> availability = new LinkedHashMap<>();
        for (Room room : rooms) {
            if (type == null || room.getType() == type) {
                availability.put(room, isAvailable(room, checkIn, checkOut));
            }
        }
        return availability;
    }

    public Room findRoom(int number) {
        return rooms.stream()
                .filter(room -> room.getNumber() == number)
                .findFirst()
                .orElseThrow(() -> new ReservationNotFoundException("Room " + number + " does not exist"));
    }

    public synchronized void reserve(Reservation reservation) {
        if (!isAvailable(reservation.getRoom(), reservation.getCheckIn(), reservation.getCheckOut())) {
            throw new ReservationConflictException(
                    "Room " + reservation.getRoom().getNumber() + " is already booked for those dates");
        }
        bookings.add(reservation);
    }

    public synchronized void release(Reservation reservation) {
        bookings.remove(reservation);
    }

    private boolean isAvailable(Room room, LocalDate checkIn, LocalDate checkOut) {
        return bookings.stream()
                .filter(booking -> booking.getRoom().getNumber() == room.getNumber())
                .filter(Reservation::isActive)
                .noneMatch(booking -> booking.overlaps(checkIn, checkOut));
    }
}
