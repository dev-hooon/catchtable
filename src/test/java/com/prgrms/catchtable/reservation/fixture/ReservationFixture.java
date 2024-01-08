package com.prgrms.catchtable.reservation.fixture;

import static com.prgrms.catchtable.reservation.domain.ReservationStatus.COMPLETED;

import com.prgrms.catchtable.common.data.shop.ShopData;
import com.prgrms.catchtable.reservation.domain.Reservation;
import com.prgrms.catchtable.reservation.domain.ReservationStatus;
import com.prgrms.catchtable.reservation.domain.ReservationTime;
import com.prgrms.catchtable.reservation.dto.request.CreateReservationRequest;
import com.prgrms.catchtable.reservation.dto.request.ModifyReservationRequest;
import com.prgrms.catchtable.reservation.dto.request.ModifyReservationStatusRequest;
import com.prgrms.catchtable.shop.domain.Shop;
import java.time.LocalDateTime;
import org.springframework.test.util.ReflectionTestUtils;

public class ReservationFixture {


    public static Reservation getReservation(ReservationTime reservationTime) {
        if (!reservationTime.isOccupied()) {
            reservationTime.setOccupiedTrue();
        }
        return Reservation.builder()
            .status(COMPLETED)
            .peopleCount(4)
            .reservationTime(reservationTime)
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
        reservationTime.setPreOccupiedTrue();
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

    public static ModifyReservationRequest getModifyReservationRequest(Long reservationTimeId) {
        return ModifyReservationRequest.builder()
            .reservationTimeId(reservationTimeId)
            .peopleCount(2)
            .build();
    }


    public static ReservationTime getAnotherReservationTimeNotPreOccupied() {
        ReservationTime reservationTime = ReservationTime.builder()
            .time(LocalDateTime.of(2024, 11, 30, 19, 30))
            .build();
        reservationTime.insertShop(ShopData.getShop());
        return reservationTime;
    }

    public static ReservationTime getReservationTimeOccupied() {
        ReservationTime reservationTime = ReservationTime.builder()
            .time(LocalDateTime.of(2024, 12, 31, 19, 30))
            .build();
        Shop shop = ShopData.getShop();
        ReflectionTestUtils.setField(shop, "id", 1L);
        reservationTime.insertShop(shop);
        reservationTime.setOccupiedTrue();
        return reservationTime;
    }

    public static ModifyReservationStatusRequest getModifyReservationStatusRequest(
        ReservationStatus status) {
        return ModifyReservationStatusRequest.builder()
            .status(status)
            .build();
    }

}
