package com.prgrms.catchtable.reservation.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import com.prgrms.catchtable.common.data.reservation.ReservationData;
import com.prgrms.catchtable.common.data.shop.ShopData;
import com.prgrms.catchtable.reservation.domain.Reservation;
import com.prgrms.catchtable.reservation.domain.ReservationTime;
import com.prgrms.catchtable.shop.domain.Shop;
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
        ReservationTime reservationTime = ReservationData.getReservationTimeNotPreOccupied();
        Shop shop = ShopData.getShop();
        Shop savedShop = shopRepository.save(shop);
        reservationTime.insertShop(savedShop);
        ReservationTime savedReservationTime = reservationTimeRepository.save(reservationTime);

        Reservation reservation = ReservationData.getReservation(savedReservationTime);
        reservationRepository.save(reservation);

        List<Reservation> reservations = reservationRepository.findAllWithReservationTimeAndShop();
        Reservation findReservation = reservations.get(0);

        assertAll(
            () -> assertThat(findReservation.getReservationTime()).isEqualTo(savedReservationTime),
            () -> assertThat(findReservation.getShop()).isEqualTo(savedShop)
        );
    }

}