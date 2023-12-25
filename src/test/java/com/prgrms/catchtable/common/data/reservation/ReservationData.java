package com.prgrms.catchtable.common.data.reservation;

import static com.prgrms.catchtable.reservation.domain.ReservationStatus.COMPLETED;

import com.prgrms.catchtable.reservation.domain.Reservation;
import com.prgrms.catchtable.reservation.domain.ReservationTime;
import com.prgrms.catchtable.reservation.dto.request.CreateReservationRequest;
import java.time.LocalDateTime;

public class ReservationData {

    public static Reservation getReservation() {
        return Reservation.builder()
            .status(COMPLETED)
            .peopleCount(4)
            .build();
    }

    public static ReservationTime getReservationTime() {
        return ReservationTime.builder()
            .time(LocalDateTime.of(2023, 12, 31, 19, 30))
            .build();
    }

    public static CreateReservationRequest getCreateReservationRequest() {
        return new CreateReservationRequest(LocalDateTime.of(2023, 12, 31, 19, 30), 5);
    }

}
