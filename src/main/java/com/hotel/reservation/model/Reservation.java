package com.hotel.reservation.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @Column(name = "reservation_id")
    private String id;

    @Column(name = "guest_name", nullable = false)
    private String guestName;

    @Column(name = "guest_email", nullable = false)
    private String guestEmail;

    @Column(name = "guest_phone", nullable = false)
    private String guestPhone;

    @Column(name = "document_id", nullable = false)
    private String documentId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "room_number", nullable = false)
    private Room room;

    @Column(name = "check_in", nullable = false)
    private LocalDate checkIn;

    @Column(name = "check_out", nullable = false)
    private LocalDate checkOut;

    @Column(name = "nights", nullable = false)
    private long nights;

    @Enumerated(EnumType.STRING)
    @Column(name = "season_type", nullable = false)
    private SeasonType season;

    @Column(name = "nightly_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal nightlyRate;

    @Column(name = "room_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal roomTotal;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "reservation_services", joinColumns = @JoinColumn(name = "reservation_id"))
    @OrderColumn(name = "service_order")
    private List<ExtraService> services = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_status", nullable = false)
    private ReservationStatus status = ReservationStatus.CONFIRMED;

    @Column(name = "digital_key")
    private String digitalKey;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "invoice_number")
    private Invoice invoice;

    protected Reservation() {
    }

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

    public boolean holdsRoom() {
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
