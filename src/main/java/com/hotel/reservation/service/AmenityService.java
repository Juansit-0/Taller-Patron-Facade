package com.hotel.reservation.service;

import com.hotel.reservation.model.ExtraService;
import com.hotel.reservation.model.ExtraServiceType;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class AmenityService {

    public ExtraService createService(ExtraServiceType type, long nights) {
        BigDecimal cost = type.getPrice().multiply(BigDecimal.valueOf(type.quantityFor(nights)));
        return new ExtraService(type, cost);
    }
}
