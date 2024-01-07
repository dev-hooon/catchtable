package com.prgrms.catchtable.reservation.repository;

import com.prgrms.catchtable.reservation.domain.Reservation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("select r from Reservation r "
        + "join fetch r.reservationTime rt "
        + "join fetch rt.shop")
    List<Reservation> findAllWithReservationTimeAndShop();

    @Query("select r from Reservation r "
        + "join fetch r.reservationTime rt "
        + "join fetch rt.shop "
        + "where r.id = :reservationId")
    Optional<Reservation> findByIdWithReservationTimeAndShop(
        @Param("reservationId") Long reservationId);

    @Query("select r from Reservation r "
        + "join fetch r.reservationTime rt "
        + "join fetch rt.shop s "
        + "where s.id = :shopId")
    List<Reservation> findAllWithReservationTimeAndShopByShopId(@Param("shopId") Long shopId);
}
