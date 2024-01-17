package com.prgrms.catchtable.reservation.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import com.prgrms.catchtable.common.data.shop.ShopData;
import com.prgrms.catchtable.member.MemberFixture;
import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.member.repository.MemberRepository;
import com.prgrms.catchtable.reservation.domain.Reservation;
import com.prgrms.catchtable.reservation.domain.ReservationTime;
import com.prgrms.catchtable.reservation.fixture.ReservationFixture;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.fixture.ShopFixture;
import com.prgrms.catchtable.shop.repository.ShopRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("예약 엔티티 조회 시 페치 조인을 통해 예약시간과 매장 엔티티를 한번에 조회한다.")
    void findAllWithReservationTimeAndShop() {
        Member member = MemberFixture.member("dlswns661035@gmail.com");
        Member savedMember = memberRepository.save(member);

        ReservationTime reservationTime = ReservationFixture.getReservationTimeNotPreOccupied();
        Shop shop = ShopData.getShop();
        Shop savedShop = shopRepository.save(shop);
        reservationTime.insertShop(savedShop);
        ReservationTime savedReservationTime = reservationTimeRepository.save(reservationTime);

        Reservation reservation = ReservationFixture.getReservation(savedReservationTime, member);
        reservationRepository.save(reservation);

        List<Reservation> reservations = reservationRepository.findAllWithReservationTimeAndShopByMemberId(
            savedMember);
        Reservation findReservation = reservations.get(0);

        assertAll(
            () -> assertThat(findReservation.getReservationTime()).isEqualTo(savedReservationTime),
            () -> assertThat(findReservation.getShop()).isEqualTo(savedShop),
            () -> assertThat(findReservation.getMember()).isEqualTo(savedMember)
        );
    }

    @Test
    @DisplayName("예약 Id를 통해 예약(예약시간, 매장까지)을 조회할 수 있다.")
    void findByIdWithReservationTimeAndShop() {
        ReservationTime reservationTime = ReservationFixture.getReservationTimeNotPreOccupied();
        Shop shop = ShopData.getShop();
        Shop savedShop = shopRepository.save(shop);
        reservationTime.insertShop(savedShop);
        ReservationTime savedReservationTime = reservationTimeRepository.save(reservationTime);

        Member member = MemberFixture.member("dlswns661035@gmail.com");
        Member savedMember = memberRepository.save(member);

        Reservation reservation = ReservationFixture.getReservationWithMember(savedReservationTime, savedMember);
        Reservation savedReservation = reservationRepository.save(reservation);

        Reservation findReservation = reservationRepository.findByIdWithReservationTimeAndShop(
            savedReservation.getId()).orElseThrow();

        assertAll(
            () -> assertThat(findReservation.getReservationTime()).isEqualTo(savedReservationTime),
            () -> assertThat(findReservation.getShop()).isEqualTo(savedShop),
            () -> assertThat(findReservation.getPeopleCount()).isEqualTo(
                savedReservation.getPeopleCount())
        );
    }

    @Test
    @DisplayName("가게 아이디와 일치하는 예약을 전체 조회할 수 있다")
    void getAllReservationByShopId() {
        /**
         * 첫번째 예제 예약 데이터 저장
         */
        ReservationTime reservationTime = ReservationFixture.getReservationTimeNotPreOccupied();
        Shop shop = ShopData.getShop();
        Shop savedShop = shopRepository.save(shop);
        reservationTime.insertShop(savedShop);
        ReservationTime savedReservationTime = reservationTimeRepository.save(reservationTime);
        Reservation reservation = ReservationFixture.getReservation(savedReservationTime);

        reservationRepository.save(reservation);
        /**
         * 두번째 예제 예약 데이터 저장 (점주의 가게 예약이 아닌 데이터)
         */
        ReservationTime otherReservationTime = ReservationFixture.getReservationTimeNotPreOccupied();
        Shop otherShop = ShopFixture.shop();
        Shop otherSavedShop = shopRepository.save(otherShop);
        otherReservationTime.insertShop(otherSavedShop);
        ReservationTime otherSavedReservationTime = reservationTimeRepository.save(
            otherReservationTime);
        Reservation otherReservation = ReservationFixture.getReservation(otherSavedReservationTime);

        reservationRepository.save(otherReservation);

        List<Reservation> all = reservationRepository.findAllWithReservationTimeAndShopByShopId(
            savedShop.getId());

        assertAll(
            () -> assertThat(all).contains(reservation),
            () -> assertThat(all).doesNotContain(otherReservation)
        );
    }

    @Test
    @DisplayName("오늘 날짜인 예약들만 가져올 수 있다.")
    void findAllTodayReservation() {
        Member member = MemberFixture.member("dls@gmail.com");
        Member savedMember = memberRepository.save(member);
        Shop shop = ShopFixture.shop();
        Shop savedShop = shopRepository.save(shop);

        LocalDateTime startOfDay = LocalDateTime.of(2024, 1, 2, 0, 0);
        LocalDateTime endOfDay = LocalDateTime.of(2024, 1, 3, 0, 0);

        ReservationTime time1 = ReservationTime.builder()
            .time(LocalDateTime.of(2024, 1, 2, 19, 30))
            .build();
        ReservationTime time2 = ReservationTime.builder()
            .time(LocalDateTime.of(2024, 1, 3, 15, 30))
            .build();

        time1.insertShop(savedShop); //예약시간 - 매장 매핑
        time2.insertShop(savedShop);
        reservationTimeRepository.saveAll(List.of(time1, time2));

        Reservation reservation1 = ReservationFixture.getReservation(time1,
            savedMember); // 예약시간 - 예약 - 회원 매핑
        Reservation reservation2 = ReservationFixture.getReservation(time2, savedMember);

        Reservation savedReservation1 = reservationRepository.save(reservation1);
        Reservation savedReservation2 = reservationRepository.save(reservation2);

        List<Reservation> reservations = reservationRepository.findAllTodayReservation(
            startOfDay,
            endOfDay
        ); //1월 2일의 예약만 조회 -> reservation1만 조회될 것

        assertThat(reservations).contains(savedReservation1); // 시간 범위 안에 있으므로 가져와야됨
        assertThat(reservations).doesNotContain(savedReservation2); // 범위 밖이니 가져오면 안됨

    }

}