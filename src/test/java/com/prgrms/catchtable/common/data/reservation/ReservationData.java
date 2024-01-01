package com.prgrms.catchtable.common.data.reservation;

import static com.prgrms.catchtable.reservation.domain.ReservationStatus.COMPLETED;

import com.prgrms.catchtable.common.data.shop.ShopData;
import com.prgrms.catchtable.reservation.domain.Reservation;
import com.prgrms.catchtable.reservation.domain.ReservationTime;
import com.prgrms.catchtable.reservation.dto.request.CreateReservationRequest;
import java.time.LocalDateTime;

public class ReservationData {

    public static Reservation getReservation() {
        return Reservation.builder()
            .status(COMPLETED)
            .peopleCount(4)
            .reservationTime(getReservationTimeNotPreOccupied())
            .build();
    }

    public static ReservationTime getReservationTimeNotPreOccupied() {
        ReservationTime reservationTime = ReservationTime.builder()
            .time(LocalDateTime.of(2024, 12, 31, 19, 30))
            .build();
        reservationTime.insertShop(ShopData.getShop());
        return reservationTime;
    }

    public static ReservationTime getReservationTimePreOccupied() {
        ReservationTime reservationTime = ReservationTime.builder()
            .time(LocalDateTime.of(2024, 12, 31, 19, 30))
            .build();
        reservationTime.insertShop(ShopData.getShop());
        reservationTime.reversePreOccupied();
        return reservationTime;
    }

    public static CreateReservationRequest getCreateReservationRequest(Long reservationTimeId) {
        return CreateReservationRequest.builder()
            .reservationTimeId(reservationTimeId)
            .peopleCount(4)
            .build();
    }

}
