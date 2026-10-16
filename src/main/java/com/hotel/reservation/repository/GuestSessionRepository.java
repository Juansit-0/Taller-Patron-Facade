package com.hotel.reservation.repository;

import com.hotel.reservation.model.GuestSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestSessionRepository extends JpaRepository<GuestSession, String> {
}
