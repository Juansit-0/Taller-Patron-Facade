package com.hotel.reservation.service;

import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AccessService {

    public String generateKey() {
        return UUID.randomUUID().toString();
    }
}
