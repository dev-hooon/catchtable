package com.prgrms.catchtable.reservation.controller;

import static com.prgrms.catchtable.reservation.domain.ReservationStatus.CANCELLED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.prgrms.catchtable.common.base.BaseIntegrationTest;
import com.prgrms.catchtable.common.data.shop.ShopData;
import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.owner.fixture.OwnerFixture;
import com.prgrms.catchtable.owner.repository.OwnerRepository;
import com.prgrms.catchtable.reservation.domain.Reservation;
import com.prgrms.catchtable.reservation.domain.ReservationTime;
import com.prgrms.catchtable.reservation.dto.request.ModifyReservationStatusRequest;
import com.prgrms.catchtable.reservation.fixture.ReservationFixture;
import com.prgrms.catchtable.reservation.repository.ReservationRepository;
import com.prgrms.catchtable.reservation.repository.ReservationTimeRepository;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.repository.ShopRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
class OwnerReservationControllerTest extends BaseIntegrationTest {

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        Shop shop = shopRepository.save(ShopData.getShop());
        ReservationTime reservationTime = ReservationFixture.getReservationTimeNotPreOccupied();
        reservationTime.insertShop(shop);
        ReservationTime savedReservationTime = reservationTimeRepository.save(reservationTime);
        savedReservationTime.reverseOccupied();
        log.info("예약 시간 차지 여부 : {}", savedReservationTime.isOccupied());
        Reservation reservation = reservationRepository.save(
            ReservationFixture.getReservation(savedReservationTime));

        ReservationTime reservationTime2 = ReservationFixture.getAnotherReservationTimeNotPreOccupied();
        reservationTime2.insertShop(shop);
        ReservationTime savedReservationTime2 = reservationTimeRepository.save(reservationTime2);
        savedReservationTime2.reverseOccupied();
        log.info("예약 시간 차지 여부 : {}", savedReservationTime.isOccupied());
        Reservation reservation2 = reservationRepository.save(
            ReservationFixture.getReservation(savedReservationTime2));

        Owner owner = OwnerFixture.getOwner();
        owner.insertShop(shop);
        ownerRepository.save(owner);
    }

    @Test
    @DisplayName("점주는 예약상태를 변경시킬 수 있다")
    void modifyReservationStatus() throws Exception {
        //given
        Reservation reservation = reservationRepository.findAll().get(0);

        ModifyReservationStatusRequest request = ModifyReservationStatusRequest.builder()
            .status(CANCELLED)
            .build();

        //then
        assertThat(reservation.getReservationTime().isOccupied()).isTrue(); // 취소처리 전엔 예약시간 차있음
        mockMvc.perform(post("/owners/shop/{reservationId}", reservation.getId())
                .contentType(APPLICATION_JSON)
                .content(asJsonString(request)))
            .andExpect(status().isOk());

        assertThat(reservation.getStatus()).isEqualTo(request.status());
        assertThat(reservation.getReservationTime().isOccupied()).isFalse(); // 취소처리 후엔 예약시간 비어있음
    }

    @Test
    @DisplayName("점주는 예약된 정보들을 전체 조회할 수 있다.")
    void getAllReservation() throws Exception {
        List<Reservation> reservations = reservationRepository.findAllWithReservationTimeAndShop();
        Reservation reservation1 = reservations.get(0);
        Reservation reservation2 = reservations.get(1);

        Owner owner = ownerRepository.findAll().get(0);

        mockMvc.perform(get("/owners/shop")
                .contentType(APPLICATION_JSON)
                .content(asJsonString(owner.getId())))
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$[0].date").value(reservation1.getReservationTime().getTime().toString()))
            .andExpect(jsonPath("$[0].peopleCount").value(reservation1.getPeopleCount()))
            .andExpect(
                jsonPath("$[1].date").value(reservation2.getReservationTime().getTime().toString()))
            .andExpect(jsonPath("$[1].peopleCount").value(reservation2.getPeopleCount()));
    }
}