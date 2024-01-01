package com.prgrms.catchtable.reservation.service;

import com.prgrms.catchtable.reservation.domain.ReservationTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReservationAsync {

    @Transactional
    public void setPreOcuppied(ReservationTime reservationTime) {
        reservationTime.reversePreOccupied();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(reservationTime::reversePreOccupied, 2, TimeUnit.SECONDS);

        scheduler.shutdown();
    }
}
