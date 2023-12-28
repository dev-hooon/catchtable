package com.prgrms.catchtable.reservation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.prgrms.catchtable.common.base.BaseIntegrationTest;
import com.prgrms.catchtable.common.data.reservation.ReservationData;
import com.prgrms.catchtable.reservation.domain.ReservationTime;
import com.prgrms.catchtable.reservation.dto.request.CreateReservationRequest;
import com.prgrms.catchtable.reservation.repository.ReservationTimeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;


class ReservationControllerTest extends BaseIntegrationTest {

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Test
    @Transactional
    @DisplayName("예약 선점 api 호출에 성공한다.")
    void preOccupyReservation() throws Exception {
        ReservationTime reservationTime = ReservationData.getReservationTimeNotPreOccupied();
        ReservationTime savedReservationTime = reservationTimeRepository.save(reservationTime);
        CreateReservationRequest request = ReservationData.getCreateReservationRequest(
            savedReservationTime.getId());

        mvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.shopName").value(reservationTime.getShop().getName()))
            .andExpect(jsonPath("$.date").value(reservationTime.getTime().toString()))
            .andExpect(jsonPath("$.peopleCount").value(request.peopleCount()));
    }

    @Test
    @Transactional
    @DisplayName("선점 api 호출 시 선점권이 획득 되었다가 지정 시간 이후에 획득이 풀린다.")
    void schedulerTest() throws Exception{
        ReservationTime reservationTime = ReservationData.getReservationTimeNotPreOccupied();
        ReservationTime savedReservationTime = reservationTimeRepository.save(reservationTime);
        CreateReservationRequest request = ReservationData.getCreateReservationRequest(
            savedReservationTime.getId());

        mvc.perform(post("/reservations")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(request)));

        Assertions.assertThat(reservationTime.isPreOccupied()).isTrue();
        Thread.sleep(3_000); //현재 스케줄러는 2초로 설정되어있어 3초간 대기 후 검증
        Assertions.assertThat(reservationTime.isPreOccupied()).isFalse();
    }


}