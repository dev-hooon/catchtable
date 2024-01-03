package com.prgrms.catchtable.reservation.service;

import static com.prgrms.catchtable.reservation.domain.ReservationStatus.COMPLETED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.prgrms.catchtable.reservation.dto.response.CreateReservationResponse;
import com.prgrms.catchtable.reservation.fixture.ReservationFixture;
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
    private ReservationRepository reservationRepository;
    @Mock
    private ReservationAsync reservationAsync;
    @Mock
    private ReservationTimeRepository reservationTimeRepository;
    @InjectMocks
    private ReservationService reservationService;

    @Test
    @DisplayName("예약시간의 선점 여부를 검증하고 선점권이 빈 것을 확인한다.")
    void validateReservation() {
        //given
        ReservationTime reservationTime = ReservationFixture.getReservationTimeNotPreOccupied();
        ReflectionTestUtils.setField(reservationTime, "id", 1L);
        CreateReservationRequest request = ReservationFixture.getCreateReservationRequestWithId(
            reservationTime.getId());

        when(reservationTimeRepository.findById(1L)).thenReturn(Optional.of(reservationTime));
        doNothing().when(reservationAsync).setPreOcuppied(reservationTime);
        //when
        CreateReservationResponse response = reservationService.preOccupyReservation(
            request);


        //then
        assertAll(
            () -> assertThat(response.shopName()).isEqualTo(reservationTime.getShop().getName()),
            () -> assertThat(response.date()).isEqualTo(reservationTime.getTime()),
            () -> assertThat(response.peopleCount()).isEqualTo(request.peopleCount())
        );


    }

    @Test
    @DisplayName("예약시간 선점권이 이미 타인에게 있는 경우 예외가 발생한다.")
    void alreadyPreOccupied() {
        //given
        ReservationTime reservationTime = ReservationFixture.getReservationTimePreOccupied();
        ReflectionTestUtils.setField(reservationTime, "id", 1L);
        CreateReservationRequest request = ReservationFixture.getCreateReservationRequestWithId(
            reservationTime.getId());

        when(reservationTimeRepository.findById(1L)).thenReturn(Optional.of(reservationTime));

        //when
        assertThrows(BadRequestCustomException.class,
            () -> reservationService.preOccupyReservation(request));


    }

    @Test
    @DisplayName("최종예약을 등록할 때 예약시간이 비었으면 성공적으로 예약 등록을 완료한다.")
    void registerReservation() {
        ReservationTime reservationTime = ReservationFixture.getReservationTimePreOccupied();
        CreateReservationRequest request = ReservationFixture.getCreateReservationRequest();
        Reservation reservation = Reservation.builder()
            .status(COMPLETED)
            .peopleCount(request.peopleCount())
            .reservationTime(reservationTime)
            .build();

        when(reservationTimeRepository.findByIdWithShop(any(Long.class))).thenReturn(
            Optional.of(reservationTime));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        CreateReservationResponse response = reservationService.registerReservation(request);

        assertAll(
            () -> assertThat(response.date()).isEqualTo(reservationTime.getTime()),
            () -> assertThat(response.shopName()).isEqualTo(reservationTime.getShop().getName()),
            () -> assertThat(response.peopleCount()).isEqualTo(request.peopleCount())
        );
    }

    @Test
    @DisplayName("최종예약을 등록할 때 타인이 이미 예약한 경우 예외가 발생한다.")
    void registerReservationAlreadyOccupied() {
        ReservationTime reservationTime = ReservationFixture.getReservationTimePreOccupied();
        CreateReservationRequest request = ReservationFixture.getCreateReservationRequest();

        reservationTime.reverseOccupied();
        when(reservationTimeRepository.findByIdWithShop(any(Long.class))).thenReturn(
            Optional.of(reservationTime));

        assertThrows(BadRequestCustomException.class,
            () -> reservationService.registerReservation(request));
    }

    @Test
    @DisplayName("예약 전체 조회를 할 수 있다")
    void getAllReservation() {
        ReservationTime reservationTime = ReservationFixture.getReservationTimeNotPreOccupied();
        Reservation reservation = ReservationFixture.getReservation(reservationTime);

        when(reservationRepository.findAllWithReservationTimeAndShop()).thenReturn(
            List.of(reservation));
        List<GetAllReservationResponse> all = reservationService.getAllReservation();
        GetAllReservationResponse findReservation = all.get(0);

        assertAll(
            () -> assertThat(findReservation.date()).isEqualTo(
                reservation.getReservationTime().getTime()),
            () -> assertThat(findReservation.peopleCount()).isEqualTo(reservation.getPeopleCount()),
            () -> assertThat(findReservation.shopName()).isEqualTo(reservation.getShop().getName()),
            () -> assertThat(findReservation.status()).isEqualTo(reservation.getStatus())
        );
    }

    @Test
    @DisplayName("예약 내역이 하나도 없을 시 조회되는 예약이 없다.")
    void getAllReservationWithNoResult() {
        when(reservationRepository.findAllWithReservationTimeAndShop()).thenReturn(List.of());

        List<GetAllReservationResponse> all = reservationService.getAllReservation();
        assertThat(all.size()).isZero();
    }

}