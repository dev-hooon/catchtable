package com.prgrms.catchtable.reservation.controller;

import static com.prgrms.catchtable.common.Role.OWNER;
import static com.prgrms.catchtable.reservation.domain.ReservationStatus.CANCELLED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.prgrms.catchtable.common.base.BaseIntegrationTest;
import com.prgrms.catchtable.common.data.shop.ShopData;
import com.prgrms.catchtable.jwt.token.Token;
import com.prgrms.catchtable.member.MemberFixture;
import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.member.repository.MemberRepository;
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
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        Shop shop = shopRepository.save(ShopData.getShop());
        ReservationTime reservationTime = ReservationFixture.getReservationTimeNotPreOccupied();
        reservationTime.insertShop(shop);
        ReservationTime savedReservationTime = reservationTimeRepository.save(reservationTime);
        savedReservationTime.setOccupiedTrue();
        log.info("예약 시간 차지 여부 : {}", savedReservationTime.isOccupied());

        Member member = MemberFixture.member("qwe@naver.com");
        Member savedMember = memberRepository.save(member);
        Reservation reservation = reservationRepository.save(
            ReservationFixture.getReservationWithMember(savedReservationTime, savedMember));

        ReservationTime reservationTime2 = ReservationFixture.getAnotherReservationTimeNotPreOccupied();
        reservationTime2.insertShop(shop);
        ReservationTime savedReservationTime2 = reservationTimeRepository.save(reservationTime2);
        savedReservationTime2.setOccupiedTrue();
        log.info("예약 시간 차지 여부 : {}", savedReservationTime.isOccupied());
        Reservation reservation2 = reservationRepository.save(
            ReservationFixture.getReservation(savedReservationTime2));

        Owner owner = OwnerFixture.getOwner("email", "password");
        owner.insertShop(shop);
        ownerRepository.save(owner);

        Token token = jwtTokenProvider.createToken(owner.getEmail(), OWNER);
        httpHeaders.add("AccessToken", token.getAccessToken());
        httpHeaders.add("RefreshToken", token.getRefreshToken());
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
        mockMvc.perform(patch("/owners/shops/{reservationId}", reservation.getId())
                .headers(httpHeaders)
                .contentType(APPLICATION_JSON)
                .content(asJsonString(request)))
            .andExpect(status().isOk());

        assertThat(reservation.getStatus()).isEqualTo(request.status());
        assertThat(reservation.getReservationTime().isOccupied()).isFalse(); // 취소처리 후엔 예약시간 비어있음
    }

    @Test
    @DisplayName("점주는 예약된 정보들을 전체 조회할 수 있다.")
    void getAllReservation() throws Exception {
        Owner owner = ownerRepository.findAll().get(0);

        List<Reservation> reservations = reservationRepository.findAllWithReservationTimeAndShopByShopId(
            owner.getShop().getId());
        Reservation reservation1 = reservations.get(0);
        Reservation reservation2 = reservations.get(1);

        mockMvc.perform(get("/owners/shops")
                .contentType(APPLICATION_JSON)
                .headers(httpHeaders)
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