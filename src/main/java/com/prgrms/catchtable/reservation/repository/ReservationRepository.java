package com.prgrms.catchtable.reservation.repository;

import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.reservation.domain.Reservation;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("select r from Reservation r "
        + "join fetch r.reservationTime rt "
        + "join fetch rt.shop "
        + "where r.member = :member")
    List<Reservation> findAllWithReservationTimeAndShopByMemberId(@Param("member") Member member);

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

    @Query("select r from Reservation r "
        + "join fetch r.member m "
        + "join fetch r.reservationTime rt "
        + "where rt.time  >= :startOfDay "
        + "and rt.time < :endOfDay " // endOfDay는 다음날 00시00분이므로 그 시간보다 미만 조건
        + "and r.status = 'COMPLETED'")
    List<Reservation> findAllTodayReservation(
        @Param("startOfDay") LocalDateTime startOfDay,
        @Param("endOfDay") LocalDateTime endOfDay
    );
}
