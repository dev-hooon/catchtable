package com.prgrms.catchtable.reservation.controller;

import static com.prgrms.catchtable.common.exception.ErrorCode.ALREADY_OCCUPIED_RESERVATION_TIME;
import static com.prgrms.catchtable.reservation.domain.ReservationStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.prgrms.catchtable.common.base.BaseIntegrationTest;
import com.prgrms.catchtable.common.data.shop.ShopData;
import com.prgrms.catchtable.reservation.domain.Reservation;
import com.prgrms.catchtable.reservation.domain.ReservationStatus;
import com.prgrms.catchtable.reservation.domain.ReservationTime;
import com.prgrms.catchtable.reservation.dto.request.CreateReservationRequest;
import com.prgrms.catchtable.reservation.dto.request.ModifyReservationRequest;
import com.prgrms.catchtable.reservation.fixture.ReservationFixture;
import com.prgrms.catchtable.reservation.repository.ReservationRepository;
import com.prgrms.catchtable.reservation.repository.ReservationTimeRepository;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.repository.ShopRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ReservationControllerTest extends BaseIntegrationTest {

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        Shop shop = ShopData.getShop();
        Shop savedShop = shopRepository.save(shop);

        ReservationTime reservationTime = ReservationFixture.getReservationTimeNotPreOccupied();
        reservationTime.insertShop(savedShop);
        reservationTimeRepository.save(reservationTime);
    }

    @Test
    @DisplayName("예약 선점 api 호출에 성공한다.")
    void preOccupyReservation() throws Exception {
        List<ReservationTime> all = reservationTimeRepository.findAll();
        ReservationTime reservationTime = all.get(0);

        CreateReservationRequest request = ReservationFixture.getCreateReservationRequestWithId(
            reservationTime.getId());

        mockMvc.perform(post("/reservations")
                .contentType(APPLICATION_JSON)
                .content(asJsonString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.shopName").value(reservationTime.getShop().getName()))
            .andExpect(jsonPath("$.date").value(reservationTime.getTime().toString()))
            .andExpect(jsonPath("$.peopleCount").value(request.peopleCount()));
    }

    @Test
    @DisplayName("선점 api 호출 시 선점권이 획득 되었다가 지정 시간 이후에 획득이 풀린다.")
    void schedulerTest() throws Exception {
        List<ReservationTime> all = reservationTimeRepository.findAll();
        ReservationTime reservationTime = all.get(0);

        CreateReservationRequest request = ReservationFixture.getCreateReservationRequestWithId(
            reservationTime.getId());

        mockMvc.perform(post("/reservations")
            .contentType(APPLICATION_JSON)
            .content(asJsonString(request)));

        assertThat(reservationTime.isPreOccupied()).isTrue();
        Thread.sleep(3_000); //현재 스케줄러는 2초로 설정되어있어 3초간 대기 후 검증
        assertThat(reservationTime.isPreOccupied()).isFalse();
    }

    @Test
    @DisplayName("예약 등록 api 호출에 성공한다.")
    void resigerReservation() throws Exception {
        List<ReservationTime> all = reservationTimeRepository.findAll();
        ReservationTime reservationTime = all.get(0);

        CreateReservationRequest request = ReservationFixture.getCreateReservationRequestWithId(
            reservationTime.getId());

        mockMvc.perform(post("/reservations/success")
                .contentType(APPLICATION_JSON)
                .content(asJsonString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.shopName").value(reservationTime.getShop().getName()))
            .andExpect(jsonPath("$.date").value(reservationTime.getTime().toString()))
            .andExpect(jsonPath("$.peopleCount").value(request.peopleCount()));

        assertThat(reservationTime.isOccupied()).isTrue();
    }

    @Test
    @DisplayName("이미 예약이 된 시간에 대해 예약 등록 api 호출 시 에러 메세지가 반환된다.")
    void registerReservationWithException() throws Exception {
        ReservationTime reservationTime = ReservationFixture.getReservationTimeNotPreOccupied();
        reservationTime.reverseOccupied();
        List<Shop> shops = shopRepository.findAll();
        Shop shop = shops.get(0);
        reservationTime.insertShop(shop);

        ReservationTime savedReservationTime = reservationTimeRepository.save(reservationTime);

        CreateReservationRequest request = ReservationFixture.getCreateReservationRequestWithId(
            savedReservationTime.getId());
        mockMvc.perform(post("/reservations/success")
                .contentType(APPLICATION_JSON)
                .content(asJsonString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(ALREADY_OCCUPIED_RESERVATION_TIME.getMessage()));

    }

    @Test
    @DisplayName("예약 수정 api 호출에 성공한다.")
    void modifyReservation() throws Exception {
        ReservationTime reservationTime = reservationTimeRepository.findAll().get(0);
        Reservation reservation = ReservationFixture.getReservation(reservationTime);
        Reservation savedReservation = reservationRepository.save(reservation);

        ModifyReservationRequest request = ReservationFixture.getModifyReservationRequest(
            reservationTime.getId());

        ReservationTime modifyReservationTime = reservationTimeRepository.findByIdAndShopId(
            request.reservationTimeId(), reservation.getShop().getId()).orElseThrow(); // 수정하려는 예약시간

        mockMvc.perform(patch("/reservations/{reservaionId}", savedReservation.getId())
                .contentType(APPLICATION_JSON)
                .content(asJsonString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.date").value(modifyReservationTime.getTime().toString()))
            .andExpect(jsonPath("$.peopleCount").value(request.peopleCount()));

        assertThat(savedReservation.getReservationTime()).isEqualTo(
            modifyReservationTime); // 수정하려는 예약시간으로 예약이 변경되었는 지 검증
    }

    @Test
    @DisplayName("예약 삭제 api 호출에 성공한다")
    void cancelReservation() throws Exception {
        ReservationTime reservationTime = reservationTimeRepository.findAll().get(0);
        Reservation reservation = ReservationFixture.getReservation(reservationTime);
        Reservation savedReservation = reservationRepository.save(reservation);

        mockMvc.perform(delete("/reservations/{reservationId}", savedReservation.getId())
            .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(CANCELLED.toString()));
    }

}