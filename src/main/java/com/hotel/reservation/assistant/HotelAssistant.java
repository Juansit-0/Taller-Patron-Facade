package com.hotel.reservation.assistant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.AutomaticFunctionCallingConfig;
import com.google.genai.types.Candidate;
import com.google.genai.types.Content;
import com.google.genai.types.FunctionCall;
import com.google.genai.types.FunctionResponse;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import com.hotel.reservation.dto.ChatResponse;
import com.hotel.reservation.exception.AssistantUnavailableException;
import com.hotel.reservation.facade.HotelFacade;
import com.hotel.reservation.model.Reservation;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HotelAssistant {

    private static final int MAX_TOOL_ITERATIONS = 8;
    private static final String FALLBACK_REPLY =
            "Lo siento, no puedo ayudarte con eso. ¿Hay algo más de tu reserva en lo que pueda apoyarte?";
    private static final String SYSTEM_PROMPT = """
            You are Vicky, the virtual concierge of Casa Arrayan, a boutique hotel by the sea in Taganga, Santa Marta, Colombia.
            Always reply in Spanish, with a warm and concise tone, in 2 to 5 short sentences of plain text without markdown or tables.
            Call room types Sencilla (SINGLE), Doble (DOUBLE) and Suite (SUITE).
            Base nightly prices: Sencilla 80 USD, Doble 120 USD, Suite 250 USD. High season is December, January, June and July, when prices are multiplied by 1.5.
            Extra services: SPA 50 USD, BREAKFAST 15 USD per night, TRANSFER 30 USD.
            Rules:
            - Always use the tools to check availability and prices. Never invent rooms, prices or reservation data.
            - Dates for tools use the format YYYY-MM-DD. The user message may start with today's date; use it to resolve relative dates.
            - Before creating a reservation collect the guest full name, email, phone and identity document.
            - Before calling create_reservation, check_in or check_out, summarize the action and ask the guest for explicit confirmation. Only call the tool after the guest confirms.
            - After creating a reservation, tell the guest the reservation id.
            - After check_in, tell the guest the digital key is ready.
            - After check_out, summarize the invoice total.
            - If a tool returns an error, explain the problem kindly and suggest the next step.
            """;

    private final AssistantTools assistantTools;
    private final HotelFacade hotelFacade;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;
    private final Map<String, List<Content>> conversations = new ConcurrentHashMap<>();
    private final Map<String, String> lastReservations = new ConcurrentHashMap<>();
    private Client client;

    public HotelAssistant(AssistantTools assistantTools, HotelFacade hotelFacade, ObjectMapper objectMapper,
                          @Value("${GEMINI_API_KEY:}") String apiKey,
                          @Value("${app.gemini.model}") String model) {
        this.assistantTools = assistantTools;
        this.hotelFacade = hotelFacade;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
    }

    public boolean isEnabled() {
        return !apiKey.isBlank();
    }

    public ChatResponse chat(String conversationId, String message) {
        if (!isEnabled()) {
            throw new AssistantUnavailableException("Vicky no esta disponible en este momento");
        }
        String id = conversationId == null || conversationId.isBlank() ? UUID.randomUUID().toString() : conversationId;
        List<Content> history = conversations.computeIfAbsent(id, key -> new ArrayList<>());
        synchronized (history) {
            String reply = answer(id, history, message);
            return new ChatResponse(id, reply, currentReservation(id));
        }
    }

    private String answer(String conversationId, List<Content> history, String message) {
        List<Content> turn = new ArrayList<>(history);
        String text = history.isEmpty() ? "Fecha de hoy: " + LocalDate.now() + ". " + message : message;
        turn.add(Content.builder().role("user").parts(Part.fromText(text)).build());
        for (int iteration = 0; iteration < MAX_TOOL_ITERATIONS; iteration++) {
            GenerateContentResponse response = client().models.generateContent(model, turn, config());
            Content modelContent = response.candidates().flatMap(candidates -> candidates.stream().findFirst())
                    .flatMap(Candidate::content).orElse(null);
            if (modelContent == null || modelContent.parts().isEmpty()) {
                return FALLBACK_REPLY;
            }
            turn.add(modelContent);
            List<FunctionCall> calls = response.functionCalls();
            if (calls == null || calls.isEmpty()) {
                history.clear();
                history.addAll(turn);
                return replyText(modelContent);
            }
            List<Part> results = calls.stream().map(call -> runTool(conversationId, call)).toList();
            turn.add(Content.builder().role("user").parts(results).build());
        }
        return FALLBACK_REPLY;
    }

    private Part runTool(String conversationId, FunctionCall call) {
        Map<String, Object> response;
        try {
            Object result = assistantTools.execute(call);
            if (result instanceof Reservation reservation) {
                lastReservations.put(conversationId, reservation.getId());
            }
            response = Map.of("result", objectMapper.valueToTree(result));
        } catch (RuntimeException ex) {
            response = Map.of("error", String.valueOf(ex.getMessage()));
        }
        FunctionResponse.Builder functionResponse = FunctionResponse.builder()
                .name(call.name().orElse(""))
                .response(response);
        call.id().ifPresent(functionResponse::id);
        return Part.builder().functionResponse(functionResponse.build()).build();
    }

    private String replyText(Content content) {
        String reply = content.parts().orElse(List.of()).stream()
                .filter(part -> !part.thought().orElse(false))
                .flatMap(part -> part.text().stream())
                .collect(Collectors.joining()).trim();
        return reply.isEmpty() ? FALLBACK_REPLY : reply;
    }

    private Reservation currentReservation(String conversationId) {
        String reservationId = lastReservations.get(conversationId);
        return reservationId == null ? null : hotelFacade.findReservation(reservationId);
    }

    private GenerateContentConfig config() {
        return GenerateContentConfig.builder()
                .systemInstruction(Content.fromParts(Part.fromText(SYSTEM_PROMPT)))
                .tools(assistantTools.definitions())
                .automaticFunctionCalling(AutomaticFunctionCallingConfig.builder().disable(true).build())
                .maxOutputTokens(4000)
                .build();
    }

    private synchronized Client client() {
        if (client == null) {
            client = Client.builder().apiKey(apiKey).build();
        }
        return client;
    }
}
