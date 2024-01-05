package com.prgrms.catchtable.reservation.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import com.prgrms.catchtable.common.data.shop.ShopData;
import com.prgrms.catchtable.reservation.domain.Reservation;
import com.prgrms.catchtable.reservation.domain.ReservationTime;
import com.prgrms.catchtable.reservation.fixture.ReservationFixture;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.fixture.ShopFixture;
import com.prgrms.catchtable.shop.repository.ShopRepository;
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

    @Test
    @DisplayName("예약 엔티티 조회 시 페치 조인을 통해 예약시간과 매장 엔티티를 한번에 조회한다.")
    void findAllWithReservationTimeAndShop() {
        ReservationTime reservationTime = ReservationFixture.getReservationTimeNotPreOccupied();
        Shop shop = ShopData.getShop();
        Shop savedShop = shopRepository.save(shop);
        reservationTime.insertShop(savedShop);
        ReservationTime savedReservationTime = reservationTimeRepository.save(reservationTime);

        Reservation reservation = ReservationFixture.getReservation(savedReservationTime);
        reservationRepository.save(reservation);

        List<Reservation> reservations = reservationRepository.findAllWithReservationTimeAndShop();
        Reservation findReservation = reservations.get(0);

        assertAll(
            () -> assertThat(findReservation.getReservationTime()).isEqualTo(savedReservationTime),
            () -> assertThat(findReservation.getShop()).isEqualTo(savedShop)
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

        Reservation reservation = ReservationFixture.getReservation(savedReservationTime);
        Reservation savedReservation = reservationRepository.save(reservation);

        Reservation findReservation = reservationRepository.findByIdWithReservationTimeAndShop(
            savedReservation.getId()).orElseThrow();

        assertAll(
            () -> assertThat(findReservation.getReservationTime()).isEqualTo(savedReservationTime),
            () -> assertThat(findReservation.getShop()).isEqualTo(savedShop),
            () -> assertThat(findReservation.getPeopleCount()).isEqualTo(savedReservation.getPeopleCount())
        );
    }

    @Test
    @DisplayName("가게 아이디와 일치하는 예약을 전체 조회할 수 있다")
    void getAllReservationByShopId(){
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
        ReservationTime otherSavedReservationTime = reservationTimeRepository.save(otherReservationTime);
        Reservation otherReservation = ReservationFixture.getReservation(otherSavedReservationTime);

        reservationRepository.save(otherReservation);

        List<Reservation> all = reservationRepository.findAllWithReservationTimeAndShopByShopId(
            savedShop.getId());

        assertAll(
            () -> assertThat(all).contains(reservation),
            () -> assertThat(all).doesNotContain(otherReservation)
        );
    }

}