package com.prgrms.catchtable.common.data.reservation;

import static com.prgrms.catchtable.reservation.domain.ReservationStatus.COMPLETED;

import com.prgrms.catchtable.common.data.shop.ShopData;
import com.prgrms.catchtable.reservation.domain.Reservation;
import com.prgrms.catchtable.reservation.domain.ReservationTime;
import com.prgrms.catchtable.reservation.dto.request.CreateReservationRequest;
import com.prgrms.catchtable.shop.domain.Shop;
import java.time.LocalDateTime;
import org.springframework.test.util.ReflectionTestUtils;

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
        Shop shop = ShopData.getShop();
        ReflectionTestUtils.setField(shop, "id", 1L);
        reservationTime.insertShop(shop);
        reservationTime.reversePreOccupied();
        return reservationTime;
    }

    public static CreateReservationRequest getCreateReservationRequestWithId(
        Long reservationTimeId) {
        return CreateReservationRequest.builder()
            .reservationTimeId(reservationTimeId)
            .peopleCount(4)
            .build();
    }

    public static CreateReservationRequest getCreateReservationRequest() {
        return CreateReservationRequest.builder()
            .reservationTimeId(1L)
            .peopleCount(4)
            .build();
    }

}
