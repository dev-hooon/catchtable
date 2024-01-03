package com.prgrms.catchtable.reservation.repository;

import com.prgrms.catchtable.reservation.domain.Reservation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("select r from Reservation r "
        + "join fetch r.reservationTime rt "
        + "join fetch rt.shop")
    List<Reservation> findAllWithReservationTimeAndShop();
}
