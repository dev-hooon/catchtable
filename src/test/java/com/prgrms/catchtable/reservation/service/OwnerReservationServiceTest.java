package com.prgrms.catchtable.reservation.service;

import static com.prgrms.catchtable.reservation.domain.ReservationStatus.CANCELLED;
import static com.prgrms.catchtable.reservation.domain.ReservationStatus.NO_SHOW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.prgrms.catchtable.common.exception.custom.NotFoundCustomException;
import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.owner.fixture.OwnerFixture;
import com.prgrms.catchtable.owner.repository.OwnerRepository;
import com.prgrms.catchtable.reservation.domain.Reservation;
import com.prgrms.catchtable.reservation.domain.ReservationTime;
import com.prgrms.catchtable.reservation.dto.request.ModifyReservationStatusRequest;
import com.prgrms.catchtable.reservation.dto.response.OwnerGetAllReservationResponse;
import com.prgrms.catchtable.reservation.fixture.ReservationFixture;
import com.prgrms.catchtable.reservation.repository.ReservationRepository;
import com.prgrms.catchtable.shop.domain.Shop;
import java.util.ArrayList;
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
class OwnerReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private OwnerRepository ownerRepository;
    @InjectMocks
    private OwnerReservationService ownerReservationService;

    @Test
    @DisplayName("점주는 특정 예약을 노쇼처리 할 수 있다.")
    void noshowReservation() {
        ReservationTime reservationTime = ReservationFixture.getReservationTimeOccupied();
        Reservation reservation = ReservationFixture.getReservation(reservationTime);
        ModifyReservationStatusRequest request = ReservationFixture.getModifyReservationStatusRequest(
            NO_SHOW);

        when(reservationRepository.findByIdWithReservationTimeAndShop(any(Long.class))).thenReturn(
            Optional.of(reservation));

        ownerReservationService.modifyReservationStatus(1L, request);

        assertAll(
            () -> assertThat(reservation.getStatus()).isEqualTo(NO_SHOW), // 예약 상태가 노쇼로 바뀌어야함
            () -> assertThat(reservation.getReservationTime().isOccupied()).isFalse()
            // 예약한 예약시간의 차지여부가 false가 되어야함
        );
    }

    @Test
    @DisplayName("점주는 특정 예약을 취소처리 할 수 있다.")
    void cancelReservation() {
        ReservationTime reservationTime = ReservationFixture.getReservationTimeOccupied();
        Reservation reservation = ReservationFixture.getReservation(reservationTime);
        ModifyReservationStatusRequest request = ReservationFixture.getModifyReservationStatusRequest(
            CANCELLED);

        when(reservationRepository.findByIdWithReservationTimeAndShop(any(Long.class))).thenReturn(
            Optional.of(reservation));

        ownerReservationService.modifyReservationStatus(1L, request);

        assertAll(
            () -> assertThat(reservation.getStatus()).isEqualTo(CANCELLED),
            () -> assertThat(reservation.getReservationTime().isOccupied()).isFalse()
        );
    }

    @Test
    @DisplayName("존재하지 않는 예약을 노쇼,취소 처리하려 하면 예외가 발생한다")
    void modifyReservationNotExist() {
        ModifyReservationStatusRequest request = ReservationFixture.getModifyReservationStatusRequest(
            CANCELLED);

        when(reservationRepository.findByIdWithReservationTimeAndShop(any(Long.class))).thenReturn(
            Optional.empty());

        assertThrows(NotFoundCustomException.class,
            () -> ownerReservationService.modifyReservationStatus(1L, request));
    }

    @Test
    @DisplayName("점주는 가게의 예약을 전체 조회할 수 있다")
    void getAllReservation() {
        List<Reservation> reservations = new ArrayList<>();
        ReservationTime reservationTime1 = ReservationFixture.getReservationTimeNotPreOccupied();
        ReservationTime reservationTime2 = ReservationFixture.getAnotherReservationTimeNotPreOccupied();

        Shop shop1 = reservationTime1.getShop();
        Shop shop2 = reservationTime2.getShop();
        ReflectionTestUtils.setField(shop1, "id", 1L);
        ReflectionTestUtils.setField(shop2, "id", 2L);

        Reservation reservation1 = ReservationFixture.getReservation(reservationTime1);
        Reservation reservation2 = ReservationFixture.getReservation(reservationTime2);

        reservations.add(reservation1);
        reservations.add(reservation2);
        Owner owner = OwnerFixture.getOwner("email", "password");
        when(reservationRepository.findAllWithReservationTimeAndShopByShopId(
            any(Long.class))).thenReturn(reservations);
        List<OwnerGetAllReservationResponse> allReservation = ownerReservationService.getAllReservation(
            owner);

        assertAll(
            () -> assertThat(allReservation.get(0).date()).isEqualTo(
                reservation1.getReservationTime().getTime()),
            () -> assertThat(allReservation.get(1).date()).isEqualTo(
                reservation2.getReservationTime().getTime())
        );
    }

    @Test
    @DisplayName("매장에 예약이 없을 시 빈 리스트가 조회된다.")
    void getAllReservationEmpty() {
        Owner owner = OwnerFixture.getOwner("email", "password");

        when(reservationRepository.findAllWithReservationTimeAndShopByShopId(
            any(Long.class))).thenReturn(List.of());

        List<OwnerGetAllReservationResponse> allReservation = ownerReservationService.getAllReservation(
            owner);

        assertThat(allReservation).isEmpty();
    }

}