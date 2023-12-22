package com.prgrms.catchtable.reservation.repository;

import com.prgrms.catchtable.reservation.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

}
