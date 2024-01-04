package com.prgrms.catchtable.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.prgrms.catchtable.common.data.shop.ShopData;
import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import com.prgrms.catchtable.reservation.domain.ReservationTime;
import com.prgrms.catchtable.reservation.dto.request.CreateReservationRequest;
import com.prgrms.catchtable.reservation.fixture.ReservationFixture;
import com.prgrms.catchtable.reservation.repository.ReservationTimeRepository;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.repository.ShopRepository;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ReservationServiceIntegrationTest {

    @Autowired
    private ReservationService reservationService;
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
    @DisplayName("동시에 요청이 들어오면 하나만 선점권이 true로 바뀌고 나머진 예외가 발생한다.")
    void concurrencyTest() throws InterruptedException {
        AtomicInteger errorCount = new AtomicInteger(0);

        List<ReservationTime> all = reservationTimeRepository.findAll();
        ReservationTime reservationTime = all.get(0);

        CreateReservationRequest request = ReservationFixture.getCreateReservationRequestWithId(
            reservationTime.getId());
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    reservationService.preOccupyReservation(request);
                } catch (BadRequestCustomException e) {
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        assertThat(errorCount.get()).isEqualTo(threadCount - 1);

    }

}
