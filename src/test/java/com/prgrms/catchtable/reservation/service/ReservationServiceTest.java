package com.prgrms.catchtable.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.prgrms.catchtable.common.data.reservation.ReservationData;
import com.prgrms.catchtable.common.data.shop.ShopData;
import com.prgrms.catchtable.reservation.domain.Reservation;
import com.prgrms.catchtable.reservation.dto.request.CreateReservationRequest;
import com.prgrms.catchtable.reservation.dto.response.ValidateReservationResponse;
import com.prgrms.catchtable.reservation.repository.ReservationRepository;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.repository.ShopRepository;
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
    private ShopRepository shopRepository;
    @InjectMocks
    private ReservationService reservationService;

    @Test
    @DisplayName("예약시간의 선점 여부를 검증하고 선점권이 빈 것을 확인한다.")
    void validateReservation() {
        Reservation reservation = ReservationData.getReservation();
        CreateReservationRequest createReservationRequest = ReservationData.getCreateReservationRequest();
        Shop shop = ShopData.getShop();

        reservation.insertShop(shop);
        reservation.insertReservvationTime(ReservationData.getReservationTime());

        ReflectionTestUtils.setField(shop, "id", 1L);

        when(shopRepository.findById(any(Long.class))).thenReturn(Optional.of(shop));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        ValidateReservationResponse response = reservationService.validateReservationIsPossible(
            shop.getId(), createReservationRequest);
        assertAll(
            () -> assertThat(response.reservationTime().getTime()).isEqualTo(createReservationRequest.date()),
            () -> assertThat(response.shopName()).isEqualTo(shop.getName())
        );
    }
}