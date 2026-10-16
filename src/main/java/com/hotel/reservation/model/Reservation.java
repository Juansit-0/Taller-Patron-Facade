package com.hotel.reservation.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Reservation {

    private final String id;
    private final String guestName;
    private final String guestEmail;
    private final String guestPhone;
    private final String documentId;
    private final Room room;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final long nights;
    private final SeasonType season;
    private final BigDecimal nightlyRate;
    private final BigDecimal roomTotal;
    private final List<ExtraService> services = new ArrayList<>();
    private ReservationStatus status = ReservationStatus.CONFIRMED;
    private String digitalKey;
    private Invoice invoice;

    public Reservation(String id, String guestName, String guestEmail, String guestPhone, String documentId,
                       Room room, LocalDate checkIn, LocalDate checkOut, long nights, SeasonType season,
                       BigDecimal nightlyRate, BigDecimal roomTotal) {
        this.id = id;
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.guestPhone = guestPhone;
        this.documentId = documentId;
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.nights = nights;
        this.season = season;
        this.nightlyRate = nightlyRate;
        this.roomTotal = roomTotal;
    }

    public boolean overlaps(LocalDate otherCheckIn, LocalDate otherCheckOut) {
        return checkIn.isBefore(otherCheckOut) && otherCheckIn.isBefore(checkOut);
    }

    public boolean isActive() {
        return status == ReservationStatus.CONFIRMED || status == ReservationStatus.CHECKED_IN;
    }

    public void addService(ExtraService service) {
        services.add(service);
    }

    public String getId() {
        return id;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public String getGuestPhone() {
        return guestPhone;
    }

    public String getDocumentId() {
        return documentId;
    }

    public Room getRoom() {
        return room;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public long getNights() {
        return nights;
    }

    public SeasonType getSeason() {
        return season;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public List<ExtraService> getServices() {
        return services;
    }

    public BigDecimal getRoomTotal() {
        return roomTotal;
    }

    public BigDecimal getNightlyRate() {
        return nightlyRate;
    }

    public String getDigitalKey() {
        return digitalKey;
    }

    public void setDigitalKey(String digitalKey) {
        this.digitalKey = digitalKey;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }
}
