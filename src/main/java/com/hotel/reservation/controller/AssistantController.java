package com.hotel.reservation.controller;

import com.hotel.reservation.assistant.HotelAssistant;
import com.hotel.reservation.dto.ChatRequest;
import com.hotel.reservation.dto.ChatResponse;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hotel/assistant")
public class AssistantController {

    private final HotelAssistant hotelAssistant;

    public AssistantController(HotelAssistant hotelAssistant) {
        this.hotelAssistant = hotelAssistant;
    }

    @GetMapping("/status")
    public Map<String, Boolean> status() {
        return Map.of("enabled", hotelAssistant.isEnabled());
    }

    @PostMapping("/chat")
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        return hotelAssistant.chat(request.conversationId(), request.message());
    }
}
