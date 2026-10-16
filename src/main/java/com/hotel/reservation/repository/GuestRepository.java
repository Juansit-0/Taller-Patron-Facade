package com.hotel.reservation.repository;

import com.hotel.reservation.model.Guest;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {

    Optional<Guest> findByEmail(String email);
}
