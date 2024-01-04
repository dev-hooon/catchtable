package com.prgrms.catchtable.reservation.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.prgrms.catchtable.reservation.fixture.ReservationFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReservationTest {

    @Test
    @DisplayName("예약 정보 수정에 성공한다.")
    void modifyReservationEntity() {
        ReservationTime reservationTime = ReservationFixture.getReservationTimeNotPreOccupied();
        Reservation reservation = ReservationFixture.getReservation(reservationTime);

        ReservationTime modifyReservationTime = ReservationFixture.getAnotherReservationTimeNotPreOccupied();
        int modifyPeopleCount = 10;

        reservation.modifyReservation(modifyReservationTime, modifyPeopleCount);

        assertAll(
            () -> assertThat(reservation.getReservationTime()).isEqualTo(modifyReservationTime),
            () -> assertThat(reservation.getPeopleCount()).isEqualTo(modifyPeopleCount)
        );
    }

}