package com.prgrms.catchtable.reservation.service;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

import com.prgrms.catchtable.reservation.domain.ReservationTime;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReservationAsync {

    @Async
    @Transactional(propagation = REQUIRES_NEW)
    public void setPreOcuppied(ReservationTime reservationTime) {
        reservationTime.reversePreOccupied();

        try {
            Thread.sleep(5_000);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }

        reservationTime.reversePreOccupied();
    }

}
