package com.prgrms.catchtable.reservation.repository;

import com.prgrms.catchtable.reservation.domain.ReservationTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationTimeRepository extends JpaRepository<ReservationTime, Long> {
    @Query("select rt from ReservationTime rt join fetch rt.shop s where rt.id = :id")
    Optional<ReservationTime> findByIdWithShop(@Param("id") Long id);
}
