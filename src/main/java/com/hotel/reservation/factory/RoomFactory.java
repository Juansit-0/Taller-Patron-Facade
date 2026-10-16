package com.hotel.reservation.factory;

import com.hotel.reservation.model.Room;
import com.hotel.reservation.model.RoomType;
import java.util.ArrayList;
import java.util.List;

public class RoomFactory {

    private RoomFactory() {
    }

    public static Room createRoom(int number, RoomType type) {
        return new Room(number, type, number / 100);
    }

    public static List<Room> createRooms() {
        List<Room> rooms = new ArrayList<>();
        rooms.add(createRoom(101, RoomType.SINGLE));
        rooms.add(createRoom(102, RoomType.SINGLE));
        rooms.add(createRoom(103, RoomType.SINGLE));
        rooms.add(createRoom(104, RoomType.SINGLE));
        rooms.add(createRoom(105, RoomType.DOUBLE));
        rooms.add(createRoom(201, RoomType.SINGLE));
        rooms.add(createRoom(202, RoomType.SINGLE));
        rooms.add(createRoom(203, RoomType.DOUBLE));
        rooms.add(createRoom(204, RoomType.DOUBLE));
        rooms.add(createRoom(205, RoomType.DOUBLE));
        rooms.add(createRoom(301, RoomType.DOUBLE));
        rooms.add(createRoom(302, RoomType.DOUBLE));
        rooms.add(createRoom(303, RoomType.SUITE));
        rooms.add(createRoom(304, RoomType.SUITE));
        rooms.add(createRoom(305, RoomType.SUITE));
        return rooms;
    }
}
