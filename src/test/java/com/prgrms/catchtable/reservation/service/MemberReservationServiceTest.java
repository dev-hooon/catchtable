package com.prgrms.catchtable.reservation.service;

import static com.prgrms.catchtable.reservation.domain.ReservationStatus.CANCELLED;
import static com.prgrms.catchtable.reservation.domain.ReservationStatus.COMPLETED;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.prgrms.catchtable.common.data.shop.ShopData;
import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import com.prgrms.catchtable.common.exception.custom.NotFoundCustomException;
import com.prgrms.catchtable.reservation.domain.Reservation;
import com.prgrms.catchtable.reservation.domain.ReservationTime;
import com.prgrms.catchtable.reservation.dto.request.CreateReservationRequest;
import com.prgrms.catchtable.reservation.dto.request.ModifyReservationRequest;
import com.prgrms.catchtable.reservation.dto.response.CancelReservationResponse;
import com.prgrms.catchtable.reservation.dto.response.CreateReservationResponse;
import com.prgrms.catchtable.reservation.dto.response.GetAllReservationResponse;
import com.prgrms.catchtable.reservation.dto.response.ModifyReservationResponse;
import com.prgrms.catchtable.reservation.fixture.ReservationFixture;
import com.prgrms.catchtable.reservation.repository.ReservationLockRepository;
import com.prgrms.catchtable.reservation.repository.ReservationRepository;
import com.prgrms.catchtable.reservation.repository.ReservationTimeRepository;
import com.prgrms.catchtable.shop.domain.Shop;
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
class MemberReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ReservationLockRepository reservationLockRepository;
    @Mock
    private ReservationAsync reservationAsync;
    @Mock
    private ReservationTimeRepository reservationTimeRepository;
    @InjectMocks
    private MemberReservationService memberReservationService;

    @Test
    @DisplayName("예약시간의 선점 여부를 검증하고 선점권이 빈 것을 확인한다.")
    void validateReservation() {
        //given
        ReservationTime reservationTime = ReservationFixture.getReservationTimeNotPreOccupied();
        ReflectionTestUtils.setField(reservationTime, "id", 1L);
        CreateReservationRequest request = ReservationFixture.getCreateReservationRequestWithId(
            reservationTime.getId());

        when(reservationTimeRepository.findById(1L)).thenReturn(Optional.of(reservationTime));
        when(reservationLockRepository.lock(1L)).thenReturn(TRUE);
        when(reservationLockRepository.unlock(1L)).thenReturn(TRUE);
        doNothing().when(reservationAsync).setPreOcuppied(reservationTime);
        //when
        CreateReservationResponse response = memberReservationService.preOccupyReservation(
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
        when(reservationLockRepository.lock(1L)).thenReturn(TRUE);

        //when
        assertThrows(BadRequestCustomException.class,
            () -> memberReservationService.preOccupyReservation(request));


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

        CreateReservationResponse response = memberReservationService.registerReservation(request);

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
            () -> memberReservationService.registerReservation(request));
    }

    @Test
    @DisplayName("예약 전체 조회를 할 수 있다")
    void getAllReservation() {
        ReservationTime reservationTime = ReservationFixture.getReservationTimeNotPreOccupied();
        Reservation reservation = ReservationFixture.getReservation(reservationTime);

        when(reservationRepository.findAllWithReservationTimeAndShop()).thenReturn(
            List.of(reservation));
        List<GetAllReservationResponse> all = memberReservationService.getAllReservation();
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

        List<GetAllReservationResponse> all = memberReservationService.getAllReservation();
        assertThat(all).isEmpty();
    }

    @Test
    @DisplayName("예약 수정을 성공한다.")
    void modifyReservation() {
        //given
        Shop shop = ShopData.getShop();
        ReflectionTestUtils.setField(shop, "id", 1L); //shop 생성

        ReservationTime reservationTime = ReservationFixture.getReservationTimeNotPreOccupied();
        reservationTime.insertShop(shop);
        reservationTime.setOccupiedTrue(); // 수정 전 예약시간은 예약이 차있는 걸로 되어있어야함
        ReflectionTestUtils.setField(reservationTime, "id", 1L); // 수정 전 예약시간 객체 -> Id : 1

        ReservationTime modifyTime = ReservationFixture.getAnotherReservationTimeNotPreOccupied();
        ReflectionTestUtils.setField(modifyTime, "id", 2L); //수정하려는 예약시간 객체 -> Id : 2

        Reservation reservation = ReservationFixture.getReservation(reservationTime);
        ModifyReservationRequest request = ReservationFixture.getModifyReservationRequest(
            2L);

        when(reservationRepository.findByIdWithReservationTimeAndShop(1L)).thenReturn(
            Optional.of(reservation));
        when(reservationTimeRepository.findByIdAndShopId(2L, 1L)).thenReturn(
            Optional.of(modifyTime));

        //when
        ModifyReservationResponse response = memberReservationService.modifyReservation(
            1L, request); // 예약 id가 1인 예약 정보 변경

        //then
        assertAll(
            () -> assertThat(reservationTime.isOccupied()).isFalse(),
            // 수정 후 기존 예약시간이 예약가능으로 바뀌었는 지 검증
            () -> assertThat(response.date()).isEqualTo(modifyTime.getTime()),
            () -> assertThat(response.peopleCount()).isEqualTo(reservation.getPeopleCount()),
            () -> assertThat(reservation.getReservationTime()).isEqualTo(modifyTime)
        );
    }

    @Test
    @DisplayName("존재하지 않는 예약에 대한 수정을 요청할 경우 예외가 발생한다.")
    void modifyReservationNotExist() {
        ModifyReservationRequest request = ReservationFixture.getModifyReservationRequest(1L);
        when(reservationRepository.findByIdWithReservationTimeAndShop(1L)).thenReturn(
            Optional.empty());

        assertThrows(BadRequestCustomException.class,
            () -> memberReservationService.modifyReservation(1L, request));
    }

    @Test
    @DisplayName("타인에게 선점된 상태인 예약시간으로 변경하려 하면 예외가 발생한다.")
    void modifyReservationPreOccupied() {
        ReservationTime reservationTime = ReservationFixture.getReservationTimePreOccupied();
        ModifyReservationRequest request = ReservationFixture.getModifyReservationRequest(1L);
        Reservation reservation = ReservationFixture.getReservation(reservationTime);

        when(reservationRepository.findByIdWithReservationTimeAndShop(1L)).thenReturn(
            Optional.of(reservation));
        when(reservationTimeRepository.findByIdAndShopId(any(Long.class),
            any(Long.class))).thenReturn(Optional.of(reservationTime));

        assertThrows(BadRequestCustomException.class,
            () -> memberReservationService.modifyReservation(1L, request));
    }

    @Test
    @DisplayName("타인이 이미 예약한 시간으로 변경하려 하면 예외가 발생한다.")
    void modifyReservationOccupied() {
        ReservationTime reservationTime = ReservationFixture.getReservationTimeOccupied();
        ModifyReservationRequest request = ReservationFixture.getModifyReservationRequest(1L);
        Reservation reservation = ReservationFixture.getReservation(reservationTime);

        when(reservationRepository.findByIdWithReservationTimeAndShop(1L)).thenReturn(
            Optional.of(reservation));
        when(reservationTimeRepository.findByIdAndShopId(any(Long.class),
            any(Long.class))).thenReturn(Optional.of(reservationTime));

        assertThrows(BadRequestCustomException.class,
            () -> memberReservationService.modifyReservation(1L, request));
    }

    @Test
    @DisplayName("예약을 취소할 수 있다")
    void cancelReservation() {
        //given
        ReservationTime reservationTime = ReservationFixture.getReservationTimeOccupied();
        ModifyReservationRequest request = ReservationFixture.getModifyReservationRequest(1L);
        Reservation reservation = ReservationFixture.getReservation(reservationTime);
        ReflectionTestUtils.setField(reservation, "id", 1L);

        when(reservationRepository.findByIdWithReservationTimeAndShop(1L)).thenReturn(
            Optional.of(reservation));

        //when
        CancelReservationResponse response = memberReservationService.cancelReservation(
            reservation.getId());

        //then
        assertAll(
            () -> assertThat(reservation.getStatus()).isEqualTo(CANCELLED),
            () -> assertThat(reservation.getReservationTime().isOccupied()).isFalse(),
            () -> assertThat(response.status()).isEqualTo(CANCELLED)
        );

    }

    @Test
    @DisplayName("존재하지 않는 예약에 대한 삭제 요청 시 예외가 발생한다")
    void cancelReservationNotExist() {
        when(reservationRepository.findByIdWithReservationTimeAndShop(1L)).thenReturn(
            Optional.empty());

        assertThrows(NotFoundCustomException.class,
            () -> memberReservationService.cancelReservation(1L));
    }

}