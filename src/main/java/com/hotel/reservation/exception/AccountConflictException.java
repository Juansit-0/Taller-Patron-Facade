package com.hotel.reservation.exception;

public class AccountConflictException extends RuntimeException {

    public AccountConflictException(String message) {
        super(message);
    }
}
