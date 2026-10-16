package com.hotel.reservation.assistant;

import com.google.genai.types.FunctionCall;
import com.google.genai.types.FunctionDeclaration;
import com.google.genai.types.Schema;
import com.google.genai.types.Tool;
import com.google.genai.types.Type;
import com.hotel.reservation.dto.BookingRequest;
import com.hotel.reservation.dto.RoomAvailability;
import com.hotel.reservation.facade.HotelFacade;
import com.hotel.reservation.model.ExtraServiceType;
import com.hotel.reservation.model.Guest;
import com.hotel.reservation.model.Reservation;
import com.hotel.reservation.model.RoomType;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AssistantTools {

    private static final Schema DATE = text("Date in ISO format YYYY-MM-DD");
    private static final Schema RESERVATION_ID = text("Reservation id, for example RSV-1A2B3C4D");

    private final HotelFacade hotelFacade;

    public AssistantTools(HotelFacade hotelFacade) {
        this.hotelFacade = hotelFacade;
    }

    public Tool definitions() {
        return Tool.builder().functionDeclarations(
                function("check_availability", "List the rooms available between two dates with their total price",
                        Map.of("check_in", DATE, "check_out", DATE,
                                "room_type", Schema.builder().type(Type.Known.STRING)
                                        .enum_("SINGLE", "DOUBLE", "SUITE")
                                        .description("Optional room type filter").build()),
                        "check_in", "check_out"),
                function("create_reservation", "Create a reservation for the signed in guest in a specific room",
                        Map.of("room_number", Schema.builder().type(Type.Known.INTEGER).description("Room number").build(),
                                "check_in", DATE, "check_out", DATE),
                        "room_number", "check_in", "check_out"),
                FunctionDeclaration.builder()
                        .name("list_my_reservations")
                        .description("List the reservations of the signed in guest, newest first")
                        .build(),
                function("add_service", "Add an extra service to a reservation",
                        Map.of("reservation_id", RESERVATION_ID,
                                "service_type", Schema.builder().type(Type.Known.STRING)
                                        .enum_("SPA", "BREAKFAST", "TRANSFER").description("Extra service").build()),
                        "reservation_id", "service_type"),
                function("check_in", "Check the guest in and generate the digital room key",
                        Map.of("reservation_id", RESERVATION_ID), "reservation_id"),
                function("check_out", "Check the guest out, release the room and generate the invoice",
                        Map.of("reservation_id", RESERVATION_ID), "reservation_id"),
                function("get_reservation", "Get the current state of a reservation",
                        Map.of("reservation_id", RESERVATION_ID), "reservation_id"))
                .build();
    }

    public Object execute(Guest guest, FunctionCall call) {
        String name = call.name().orElse("");
        Map<String, Object> args = call.args().orElse(Map.of());
        return switch (name) {
            case "check_availability" -> availableRooms(args);
            case "create_reservation" -> hotelFacade.bookRoom(guest, new BookingRequest(
                    integer(args, "room_number"), date(args, "check_in"), date(args, "check_out")));
            case "list_my_reservations" -> myReservations(guest);
            case "add_service" -> hotelFacade.addService(guest, string(args, "reservation_id"),
                    ExtraServiceType.valueOf(string(args, "service_type")));
            case "check_in" -> hotelFacade.checkIn(guest, string(args, "reservation_id"));
            case "check_out" -> hotelFacade.checkOut(guest, string(args, "reservation_id"));
            case "get_reservation" -> hotelFacade.findReservation(guest, string(args, "reservation_id"));
            default -> throw new IllegalArgumentException("Unknown tool " + name);
        };
    }

    private List<Map<String, Object>> availableRooms(Map<String, Object> args) {
        Object roomType = args.get("room_type");
        RoomType type = roomType == null || roomType.toString().isBlank() ? null : RoomType.valueOf(roomType.toString());
        return hotelFacade.checkAvailability(date(args, "check_in"), date(args, "check_out"), type).stream()
                .filter(RoomAvailability::available)
                .map(room -> Map.<String, Object>of("number", room.number(), "type", room.type(),
                        "floor", room.floor(), "season", room.season(), "nights", room.nights(),
                        "totalPrice", room.totalPrice()))
                .toList();
    }

    private List<Map<String, Object>> myReservations(Guest guest) {
        return hotelFacade.findGuestReservations(guest).stream()
                .map(reservation -> Map.<String, Object>of("id", reservation.getId(),
                        "roomNumber", reservation.getRoom().getNumber(), "checkIn", reservation.getCheckIn(),
                        "checkOut", reservation.getCheckOut(), "status", reservation.getStatus(),
                        "roomTotal", reservation.getRoomTotal(), "services", serviceNames(reservation)))
                .toList();
    }

    private List<String> serviceNames(Reservation reservation) {
        return reservation.getServices().stream().map(service -> service.getType().name()).toList();
    }

    private static FunctionDeclaration function(String name, String description, Map<String, Schema> properties,
                                                String... required) {
        return FunctionDeclaration.builder()
                .name(name)
                .description(description)
                .parameters(Schema.builder().type(Type.Known.OBJECT).properties(properties).required(required).build())
                .build();
    }

    private static Schema text(String description) {
        return Schema.builder().type(Type.Known.STRING).description(description).build();
    }

    private static String string(Map<String, Object> args, String key) {
        Object value = args.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Missing argument " + key);
        }
        return value.toString().trim();
    }

    private static int integer(Map<String, Object> args, String key) {
        Object value = args.get(key);
        return value instanceof Number number ? number.intValue() : Integer.parseInt(string(args, key));
    }

    private static LocalDate date(Map<String, Object> args, String key) {
        return LocalDate.parse(string(args, key));
    }
}
