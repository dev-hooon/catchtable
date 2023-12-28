package com.prgrms.catchtable.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.prgrms.catchtable.common.data.reservation.ReservationData;
import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import com.prgrms.catchtable.reservation.domain.ReservationTime;
import com.prgrms.catchtable.reservation.dto.request.CreateReservationRequest;
import com.prgrms.catchtable.reservation.repository.ReservationTimeRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationTimeRepository reservationTimeRepository;
    @InjectMocks
    private ReservationService reservationService;

    @Test
    @DisplayName("예약시간의 선점 여부를 검증하고 선점권이 빈 것을 확인한다.")
    void validateReservation() {
        //given
        ReservationTime reservationTime = ReservationData.getReservationTimeNotPreOccupied();
        ReflectionTestUtils.setField(reservationTime, "id", 1L);
        CreateReservationRequest request = ReservationData.getCreateReservationRequest(
            reservationTime.getId());

        when(reservationTimeRepository.findById(1L)).thenReturn(Optional.of(reservationTime));

        //when
        ReservationTime savedReservationTime = reservationService.validateReservationAndSave(
            request);

        //then
        assertAll(
            () -> assertThat(savedReservationTime.getTime()).isEqualTo(reservationTime.getTime()),
            () -> assertThat(savedReservationTime.getShop()).isEqualTo(reservationTime.getShop())
        );


    }

    @Test
    @DisplayName("예약시간 선점권이 이미 타인에게 있는 경우 예외가 발생한다.")
    void alreadyPreOccupied() {
        //given
        ReservationTime reservationTime = ReservationData.getReservationTimePreOccupied();
        ReflectionTestUtils.setField(reservationTime, "id", 1L);
        CreateReservationRequest request = ReservationData.getCreateReservationRequest(
            reservationTime.getId());


        when(reservationTimeRepository.findById(1L)).thenReturn(Optional.of(reservationTime));

        //when
        assertThrows(BadRequestCustomException.class,
            () -> reservationService.validateReservationAndSave(request));


    }
}