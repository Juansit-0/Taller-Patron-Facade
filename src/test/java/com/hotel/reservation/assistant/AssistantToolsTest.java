package com.hotel.reservation.assistant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.genai.types.FunctionCall;
import com.hotel.reservation.dto.RoomAvailability;
import com.hotel.reservation.facade.HotelFacade;
import com.hotel.reservation.model.RoomType;
import com.hotel.reservation.model.SeasonType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class AssistantToolsTest {

    @Test
    void checkAvailabilityReturnsOnlyAvailableRooms() {
        HotelFacade hotelFacade = mock(HotelFacade.class);
        LocalDate checkIn = LocalDate.of(2030, 12, 10);
        LocalDate checkOut = LocalDate.of(2030, 12, 12);
        when(hotelFacade.checkAvailability(checkIn, checkOut, RoomType.SUITE)).thenReturn(List.of(
                room(303, true), room(304, false)));
        FunctionCall call = FunctionCall.builder()
                .name("check_availability")
                .args(Map.of("check_in", "2030-12-10", "check_out", "2030-12-12", "room_type", "SUITE"))
                .build();

        List<?> rooms = (List<?>) new AssistantTools(hotelFacade).execute(call);

        assertEquals(1, rooms.size());
        assertEquals(303, ((Map<?, ?>) rooms.get(0)).get("number"));
    }

    private RoomAvailability room(int number, boolean available) {
        return new RoomAvailability(number, RoomType.SUITE, 3, new BigDecimal("250.00"), available, 2,
                SeasonType.HIGH, new BigDecimal("750.00"));
    }
}
