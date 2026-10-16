package com.hotel.reservation.service;

import com.hotel.reservation.builder.InvoiceBuilder;
import com.hotel.reservation.model.ExtraService;
import com.hotel.reservation.model.Invoice;
import com.hotel.reservation.model.Reservation;
import com.hotel.reservation.model.Room;
import org.springframework.stereotype.Service;

@Service
public class BillingService {

    public Invoice generateInvoice(Reservation reservation) {
        Room room = reservation.getRoom();
        InvoiceBuilder builder = new InvoiceBuilder()
                .guestName(reservation.getGuestName())
                .addLine("Room " + room.getNumber() + " " + room.getType() + " - " + reservation.getSeason() + " season",
                        reservation.getNights(), reservation.getNightlyRate());
        for (ExtraService service : reservation.getServices()) {
            builder.addLine(service.getType().name(),
                    service.getType().quantityFor(reservation.getNights()),
                    service.getType().getPrice());
        }
        return builder.build();
    }
}
