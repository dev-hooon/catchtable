package com.prgrms.catchtable.reservation.service;

import static com.prgrms.catchtable.reservation.domain.ReservationStatus.COMPLETED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.prgrms.catchtable.common.data.reservation.ReservationData;
import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import com.prgrms.catchtable.reservation.domain.Reservation;
import com.prgrms.catchtable.reservation.domain.ReservationTime;
import com.prgrms.catchtable.reservation.dto.request.CreateReservationRequest;
import com.prgrms.catchtable.reservation.dto.response.GetAllReservationResponse;
import com.prgrms.catchtable.reservation.repository.ReservationRepository;
import com.prgrms.catchtable.reservation.repository.ReservationTimeRepository;
import java.util.List;
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
    ReservationRepository reservationRepository;
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
        CreateReservationRequest request = ReservationData.getCreateReservationRequestWithId(
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
        CreateReservationRequest request = ReservationData.getCreateReservationRequestWithId(
            reservationTime.getId());

        when(reservationTimeRepository.findById(1L)).thenReturn(Optional.of(reservationTime));

        //when
        assertThrows(BadRequestCustomException.class,
            () -> reservationService.validateReservationAndSave(request));


    }

    @Test
    @DisplayName("최종예약을 등록할 때 예약시간이 비었으면 성공적으로 예약 등록을 완료한다.")
    void registerReservation() {
        ReservationTime reservationTime = ReservationData.getReservationTimePreOccupied();
        CreateReservationRequest request = ReservationData.getCreateReservationRequest();
        Reservation reservation = Reservation.builder()
            .status(COMPLETED)
            .peopleCount(request.peopleCount())
            .reservationTime(reservationTime)
            .build();

        when(reservationTimeRepository.findByIdWithShop(any(Long.class))).thenReturn(
            Optional.of(reservationTime));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        Reservation findReservation = reservationService.validateReservationAndSaveIsEmpty(request);

        assertAll(
            () -> assertThat(findReservation.getReservationTime().getTime()).isEqualTo(
                reservationTime.getTime()),
            () -> assertThat(findReservation.getPeopleCount()).isEqualTo(request.peopleCount()),
            () -> assertThat(findReservation.getStatus()).isEqualTo(COMPLETED)
        );
    }

    @Test
    @DisplayName("최종예약을 등록할 때 타인이 이미 예약한 경우 예외가 발생한다.")
    void registerReservationAlreadyOccupied() {
        ReservationTime reservationTime = ReservationData.getReservationTimePreOccupied();
        CreateReservationRequest request = ReservationData.getCreateReservationRequest();

        reservationTime.reverseOccupied();
        when(reservationTimeRepository.findByIdWithShop(any(Long.class))).thenReturn(
            Optional.of(reservationTime));

        assertThrows(BadRequestCustomException.class,
            () -> reservationService.validateReservationAndSaveIsEmpty(request));
    }

    @Test
    @DisplayName("예약 전체 조회를 할 수 있다")
    void getAllReservation(){
        ReservationTime reservationTime = ReservationData.getReservationTimeNotPreOccupied();
        Reservation reservation = ReservationData.getReservation(reservationTime);

        when(reservationRepository.findAllWithReservationTimeAndShop()).thenReturn(List.of(reservation));
        List<GetAllReservationResponse> all = reservationService.getAllReservation();
        GetAllReservationResponse findReservation = all.get(0);

        assertAll(
            () -> assertThat(findReservation.date()).isEqualTo(reservation.getReservationTime().getTime()),
            () -> assertThat(findReservation.peopleCount()).isEqualTo(reservation.getPeopleCount()),
            () -> assertThat(findReservation.shopName()).isEqualTo(reservation.getShop().getName()),
            () -> assertThat(findReservation.status()).isEqualTo(reservation.getStatus())
        );
    }

    @Test
    @DisplayName("예약 내역이 하나도 없을 시 조회되는 예약이 없다.")
    void getAllReservationWithNoResult(){
        when(reservationRepository.findAllWithReservationTimeAndShop()).thenReturn(List.of());

        List<GetAllReservationResponse> all = reservationService.getAllReservation();
        assertThat(all.size()).isZero();
    }

}