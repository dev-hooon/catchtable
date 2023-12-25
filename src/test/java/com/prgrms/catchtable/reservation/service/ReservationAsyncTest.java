package com.prgrms.catchtable.reservation.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.prgrms.catchtable.common.data.reservation.ReservationData;
import com.prgrms.catchtable.reservation.domain.ReservationTime;
import com.prgrms.catchtable.reservation.repository.ReservationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ReservationAsyncTest {

    @Mock
    private ReservationRepository reservationRepository;
    @InjectMocks
    private ReservationAsync reservationAsync;

    @Test
    @DisplayName("예약 선점을 실행한 후 7분이 지나기 전 후로 선점여부 값이 알맞게 바뀌어야 한다.")
    void preOccupiedAsync(){
        ReservationTime reservationTime = ReservationData.getReservationTime();
        reservationAsync.setPreOcuppied(reservationTime);

        assertThat(reservationTime.isPreOccupied()).isTrue();

        try {
            Thread.sleep(6_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertThat(reservationTime.isPreOccupied()).isFalse();
    }
}