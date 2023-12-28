package com.prgrms.catchtable.reservation.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.prgrms.catchtable.common.data.reservation.ReservationData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReservationTimeTest {

    @Test
    @DisplayName("예약 선점 여부 변경에 성공한다")
    void reversePreOccupied() {
        ReservationTime reservationTime = ReservationData.getReservationTimeNotPreOccupied();
        reservationTime.reversePreOccupied();

        assertThat(reservationTime.isPreOccupied()).isTrue();
    }

    @Test
    @DisplayName("예약 여부 변경에 성공한다.")
    void reverseOccupied() {
        ReservationTime reservationTime = ReservationData.getReservationTimeNotPreOccupied();
        reservationTime.reverseOccupied();

        assertThat(reservationTime.isOccupied()).isTrue();
    }
}