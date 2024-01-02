package com.prgrms.catchtable.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.prgrms.catchtable.common.data.reservation.ReservationData;
import com.prgrms.catchtable.reservation.domain.ReservationTime;
import com.prgrms.catchtable.reservation.dto.request.CreateReservationRequest;
import com.prgrms.catchtable.reservation.dto.response.CreateReservationResponse;
import com.prgrms.catchtable.reservation.service.ReservationAsync;
import com.prgrms.catchtable.reservation.service.ReservationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReservationFacadeTest {

    @Mock
    private ReservationAsync reservationAsync;
    @Mock
    private ReservationService reservationService;
    @InjectMocks
    private ReservationFacade reservationFacade;

    @Test
    @DisplayName("예약을 검증하고 선점권을 true로 바꾸는 것에 성공한다.")
    void preOccupyReservation() {
        ReservationTime reservationTime = ReservationData.getReservationTimeNotPreOccupied();
        CreateReservationRequest request = ReservationData.getCreateReservationRequestWithId(
            reservationTime.getId());

        when(reservationService.validateReservationAndSave(
            any(CreateReservationRequest.class))).thenReturn(reservationTime);

        CreateReservationResponse response = reservationFacade.preOccupyReservation(
            request);

        assertAll(
            () -> assertThat(response.date()).isEqualTo(reservationTime.getTime()),
            () -> assertThat(response.peopleCount()).isEqualTo(request.peopleCount())
        );
    }
}