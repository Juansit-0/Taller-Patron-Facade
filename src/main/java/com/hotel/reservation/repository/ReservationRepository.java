package com.hotel.reservation.repository;

import com.hotel.reservation.model.Reservation;
import com.hotel.reservation.model.ReservationStatus;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, String> {

    List<Reservation> findByGuestIdOrderByCheckInDesc(Long guestId);

    @Query("""
            select count(r) > 0 from Reservation r
            where r.room.number = :roomNumber
              and r.status in :statuses
              and r.checkIn < :checkOut
              and :checkIn < r.checkOut
            """)
    boolean existsOverlapping(@Param("roomNumber") int roomNumber,
                              @Param("statuses") Collection<ReservationStatus> statuses,
                              @Param("checkIn") LocalDate checkIn,
                              @Param("checkOut") LocalDate checkOut);
}
