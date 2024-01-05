package com.prgrms.catchtable.reservation.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import com.prgrms.catchtable.common.data.shop.ShopData;
import com.prgrms.catchtable.common.exception.ErrorCode;
import com.prgrms.catchtable.common.exception.custom.NotFoundCustomException;
import com.prgrms.catchtable.reservation.domain.ReservationTime;
import com.prgrms.catchtable.reservation.fixture.ReservationFixture;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.fixture.ShopFixture;
import com.prgrms.catchtable.shop.repository.ShopRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class ReservationTimeRepositoryTest {

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private ShopRepository shopRepository;

    @BeforeEach
    void setUp() {
        Shop shop = ShopData.getShop();
        Shop savedShop = shopRepository.save(shop);

        ReservationTime reservationTime = ReservationFixture.getReservationTimeNotPreOccupied();
        reservationTime.insertShop(savedShop);
        reservationTimeRepository.save(reservationTime);
    }

    @Test
    @DisplayName("예약시간과 그 시간의 매장까지 한번의 쿼리로 조회할 수 있다.")
    void findReservationTimeWithShop() {
        ReservationTime reservationTime = reservationTimeRepository.findAll().get(0);

        ReservationTime findReservationTime = reservationTimeRepository.findByIdWithShop(
                reservationTime.getId())
            .orElseThrow(() -> new NotFoundCustomException(ErrorCode.NOT_EXIST_TIME));
        Shop shop = reservationTime.getShop();

        assertAll(
            () -> assertThat(findReservationTime.getShop().getName()).isEqualTo(shop.getName()),
            () -> assertThat(findReservationTime.getShop().getCapacity()).isEqualTo(
                shop.getCapacity()),
            () -> assertThat(findReservationTime.getShop().getCategory()).isEqualTo(
                shop.getCategory()),
            () -> assertThat(findReservationTime.getShop().getRating()).isEqualTo(shop.getRating())
        );
    }

    @Test
    @DisplayName("특정 매장에 특정 예약시간이 존재 하는지 조회할 수 있다")
    void findReservationTimeByShop() {
        //given
        List<ReservationTime> all = reservationTimeRepository.findAll();
        ReservationTime reservationTime = all.get(0);
        Shop shop = reservationTime.getShop();

        //when
        ReservationTime findReservationTime = reservationTimeRepository.findByIdAndShoId(
            reservationTime.getId(), shop.getId()).orElseThrow();

        //then
        assertAll(
            () -> assertThat(findReservationTime.getShop()).isEqualTo(shop),
            () -> assertThat(findReservationTime.getTime()).isEqualTo(reservationTime.getTime())
        );
    }

    @Test
    @DisplayName("조회하려는 예약시간이 해당 매장에 없는 시간이면 null이 반환된다.")
    void findReservationTimeNotInShop() {
        //given
        Shop shop = ShopFixture.shop();
        Shop savedShop = shopRepository.save(shop);
        ReservationTime reservationTime = reservationTimeRepository.findAll().get(0);

        //when
        Optional<ReservationTime> findReservationTime = reservationTimeRepository.findByIdAndShoId(
            reservationTime.getId(),
            savedShop.getId()); // 해당 예약시간은 존재하지만 찾으려는 매장의 예약시간이 아니므로 null 리턴 예상

        //then
        assertThat(findReservationTime).isEmpty();
    }
}