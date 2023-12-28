package com.prgrms.catchtable.reservation.repository;

import com.prgrms.catchtable.reservation.domain.ReservationTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationTimeRepository extends JpaRepository<ReservationTime, Long> {

}
