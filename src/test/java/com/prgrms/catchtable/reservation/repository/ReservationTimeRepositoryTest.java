package com.prgrms.catchtable.reservation.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.prgrms.catchtable.reservation.fixture.ReservationFixture;
import com.prgrms.catchtable.common.data.shop.ShopData;
import com.prgrms.catchtable.common.exception.ErrorCode;
import com.prgrms.catchtable.common.exception.custom.NotFoundCustomException;
import com.prgrms.catchtable.reservation.domain.ReservationTime;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.repository.ShopRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ReservationTimeRepositoryTest {

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private ShopRepository shopRepository;

    @Test
    @DisplayName("예약시간과 그 시간의 매장까지 한번의 쿼리로 조회할 수 있다.")
    void findReservationTimeWithShop() {
        Shop shop = ShopData.getShop();
        Shop savedShop = shopRepository.save(shop);

        ReservationTime reservationTime = ReservationFixture.getReservationTimeNotPreOccupied();
        reservationTime.insertShop(savedShop);
        ReservationTime savedTime = reservationTimeRepository.save(reservationTime);

        ReservationTime findReservationTime = reservationTimeRepository.findByIdWithShop(
                savedTime.getId())
            .orElseThrow(() -> new NotFoundCustomException(ErrorCode.NOT_EXIST_TIME));

        assertAll(
            () -> assertThat(findReservationTime.getShop().getName()).isEqualTo(shop.getName()),
            () -> assertThat(findReservationTime.getShop().getCapacity()).isEqualTo(
                shop.getCapacity()),
            () -> assertThat(findReservationTime.getShop().getCategory()).isEqualTo(
                shop.getCategory()),
            () -> assertThat(findReservationTime.getShop().getRating()).isEqualTo(shop.getRating())
        );


    }
}