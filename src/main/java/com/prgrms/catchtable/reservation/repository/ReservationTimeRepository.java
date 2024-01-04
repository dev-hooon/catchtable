package com.prgrms.catchtable.reservation.repository;

import com.prgrms.catchtable.reservation.domain.ReservationTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationTimeRepository extends JpaRepository<ReservationTime, Long> {

    @Query("select rt from ReservationTime rt join fetch rt.shop s where rt.id = :id")
    Optional<ReservationTime> findByIdWithShop(@Param("id") Long id);

    //매장에 해당 예약시간이 있는지 읽어오는 작업
    @Query("select rt from ReservationTime rt where rt.id = :reservationTimeId and rt.shop.id = :shopId")
    Optional<ReservationTime> findByIdAndShoId(@Param("reservationTimeId") Long reservationTimeId,
        @Param("shopId") Long shopId);
}
